# ----------------------------------------
# 🧠 Data Sources
# ----------------------------------------

# ECS-optimized Amazon Linux 2 AMI
data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-ecs-hvm-*-x86_64-ebs"]
  }
}

# Default subnets (to place EC2s in)
data "aws_subnets" "default" {
  filter {
    name   = "default-for-az"
    values = ["true"]
  }
}

# ----------------------------------------
# 🧠 Locals
# ----------------------------------------

# Filter only clusters with images to deploy services
locals {
  clusters_with_images = {
    for c in var.clusters : c.name => c if c.image != null
  }

  cluster_map = {
    for c in var.clusters : c.name => c
  }
}

# ----------------------------------------
# 🔐 IAM for EC2 ECS instances
# ----------------------------------------

resource "aws_iam_role" "ecs_instance_role" {
  name = "ecs-instance-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action    = "sts:AssumeRole"
      Effect    = "Allow"
      Principal = {
        Service = "ec2.amazonaws.com"
      }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_instance_attach" {
  role       = aws_iam_role.ecs_instance_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_instance_profile" "ecs_instance_profile" {
  name = "ecs-instance-profile"
  role = aws_iam_role.ecs_instance_role.name
}

# ----------------------------------------
# 🚀 ECS Clusters
# ----------------------------------------

resource "aws_ecs_cluster" "ecs" {
  for_each = local.cluster_map

  name = each.key

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  tags = var.tags
}

# ----------------------------------------
# 🧱 EC2 Launch Template
# ----------------------------------------

resource "aws_launch_template" "ecs_lt" {
  for_each = local.cluster_map

  name_prefix   = "${each.key}-lt-"
  image_id      = data.aws_ami.amazon_linux.id
  instance_type = "t3.micro"

  iam_instance_profile {
    name = aws_iam_instance_profile.ecs_instance_profile.name
  }

  user_data = base64encode(<<EOF
#!/bin/bash
echo ECS_CLUSTER=${each.key} >> /etc/ecs/ecs.config
EOF
  )

  tag_specifications {
    resource_type = "instance"
    tags = merge(var.tags, { Name = each.key })
  }
}

# ----------------------------------------
# 🔄 Auto Scaling Group (1 EC2 per cluster)
# ----------------------------------------

resource "aws_autoscaling_group" "ecs_asg" {
  for_each            = local.cluster_map
  desired_capacity    = 1
  max_size            = 1
  min_size            = 1
  vpc_zone_identifier = data.aws_subnets.default.ids

  launch_template {
    id      = aws_launch_template.ecs_lt[each.key].id
    version = "$Latest"
  }

  tag {
    key                 = "Name"
    value               = each.key
    propagate_at_launch = true
  }
}

# ----------------------------------------
# 📦 Task Definitions (only if image is provided)
# ----------------------------------------

resource "aws_ecs_task_definition" "task" {
  for_each = local.clusters_with_images

  family                   = "${each.key}-task"
  network_mode             = "bridge"
  requires_compatibilities = ["EC2"]
  cpu                      = "256"
  memory                   = "512"

  container_definitions = jsonencode([
    {
      name      = each.key
      image     = each.value.image
      essential = true
      portMappings = [
        {
          containerPort = 80
          hostPort      = 80
        }
      ]
    }
  ])
}

# ----------------------------------------
# 🧩 ECS Service (only if image is provided)
# ----------------------------------------

resource "aws_ecs_service" "svc" {
  for_each            = local.clusters_with_images
  name                = "${each.key}-service"
  cluster             = aws_ecs_cluster.ecs[each.key].id
  task_definition     = aws_ecs_task_definition.task[each.key].arn
  desired_count       = 1
  launch_type         = "EC2"
  scheduling_strategy = "REPLICA"

  deployment_minimum_healthy_percent = 50
  deployment_maximum_percent         = 200
}
