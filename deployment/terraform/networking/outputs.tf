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

output "eip_public_ip" {
  value = aws_eip.default.public_ip
  description = "Elastic IP address allocated for use in white listing MongoDB Atlas IP addresses"
}
