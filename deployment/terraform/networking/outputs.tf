output "private_subnet_1a_id" {
  value = aws_subnet.private_1a.id
}

output "private_subnet_1b_id" {
  value = aws_subnet.private_1b.id
}

output "public_subnet_1a_id" {
  value = aws_subnet.public_1a.id
}

output "public_subnet_1b_id" {
  value = aws_subnet.public_1b.id
}

output "vpc_id" {
  value = aws_vpc.main.id
}