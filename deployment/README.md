# Add EKS to kubeconfig:
`aws eks update-kubeconfig --region eu-west-1 --name communication-eks-dev-<accountID>-eu-west-1 --profile saml`

# Enable IP prefixing to allow more pods per node:
`kubectl set env daemonset aws-node -n kube-system ENABLE_PREFIX_DELEGATION=true`
- must delete and recreate all nodes after applying this