resource "aws_ecr_repository" "api" {
  name                 = "lab08-api"
  image_tag_mutability = "IMMUTABLE"
  image_scanning_configuration { scan_on_push = true }
}

data "aws_availability_zones" "available" {
  state = "available"
}

resource "aws_vpc" "lab08" {
  cidr_block           = "10.80.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = { Name = "lab08" }
}

resource "aws_subnet" "database" {
  count                   = 2
  vpc_id                  = aws_vpc.lab08.id
  cidr_block              = cidrsubnet(aws_vpc.lab08.cidr_block, 8, count.index + 10)
  availability_zone       = data.aws_availability_zones.available.names[count.index]
  map_public_ip_on_launch = false

  tags = { Name = "lab08-db-${count.index + 1}" }
}

resource "aws_db_subnet_group" "postgres" {
  name       = "lab08-postgres"
  subnet_ids = aws_subnet.database[*].id
}

resource "aws_security_group" "database" {
  name   = "lab08-postgres"
  vpc_id = aws_vpc.lab08.id

  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.app.id]
  }
}

resource "aws_db_instance" "postgres" {
  identifier             = "lab08-postgres"
  engine                 = "postgres"
  instance_class         = "db.t4g.micro"
  allocated_storage      = 20
  db_name                = "lab8"
  username               = var.db_username
  password               = var.db_password
  db_subnet_group_name   = aws_db_subnet_group.postgres.name
  vpc_security_group_ids = [aws_security_group.database.id]
  publicly_accessible    = true
  skip_final_snapshot    = true
}

variable "db_username" {
  type      = string
  sensitive = true
}
variable "db_password" {
  type      = string
  sensitive = true
}
