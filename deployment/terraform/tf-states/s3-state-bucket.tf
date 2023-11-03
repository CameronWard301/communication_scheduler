# Adapted from https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket
resource "aws_s3_bucket" "bucket" {
  bucket = "${var.bucket_name}-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}"
  lifecycle  { prevent_destroy = false }
}

resource "aws_s3_bucket_versioning" "version" {
  bucket = aws_s3_bucket.bucket.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "encryption" {
  bucket = aws_s3_bucket.bucket.id
  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_acl" "acl" {
  bucket = aws_s3_bucket.bucket.id
  acl = "private"
  depends_on = [aws_s3_bucket_ownership_controls.s3_bucket_acl_ownership]
}

resource "aws_s3_bucket_ownership_controls" "s3_bucket_acl_ownership" {
  bucket = aws_s3_bucket.bucket.id
  rule {
    object_ownership = "ObjectWriter"
  }
}

resource "aws_s3_bucket_public_access_block" "public_access_block" {
  bucket = aws_s3_bucket.bucket.id

  block_public_acls = true
  ignore_public_acls = true
  block_public_policy = true
  restrict_public_buckets = true

  depends_on = [aws_s3_bucket_policy.ssl_policy]
}

resource "aws_s3_bucket_policy" "ssl_policy" {
  bucket = aws_s3_bucket.bucket.id

  policy = jsonencode({
    "Version" = "2012-10-17"
    "Id"      = "BucketPolicy"
    "Statement" = [
      {
        "Sid"       = "EnforceHttpsAlways"
        "Effect"    = "Deny"
        "Principal" = "*"
        "Action"    = "*"
        "Resource" = [
          aws_s3_bucket.bucket.arn,
          "${aws_s3_bucket.bucket.arn}/*",
        ]
        "Condition" = {
          "Bool" = {
            "aws:SecureTransport" = "false"
          }
        }
      },
    ]
  })
}