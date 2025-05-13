variable "clusters" {
  description = "List of ECS clusters with image per cluster"
  type = list(object({
    name  = string
    image = string
  }))
}

variable "tags" {
  type    = map(string)
  default = {}
}
