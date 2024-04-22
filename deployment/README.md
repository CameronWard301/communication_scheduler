# Add EKS to kubeconfig:
`aws eks update-kubeconfig --region eu-west-1 --name communication-eks --profile saml`

# Enable IP prefixing to allow more pods per node:
`kubectl set env daemonset aws-node -n kube-system ENABLE_PREFIX_DELEGATION=true`
- must delete and recreate all nodes after applying this

# Enable GithubActionUser to access EKS:
Run `kubectl edit configmap aws-auth --namespace kube-system` and add the following to the mapUsers section:
```kubernetes helm
apiVersion: v1
data:
  mapRoles: |
    # might have other roles here
  mapUsers: |
    - userarn: <user arn>
      username: <username>
      groups:
        - system:masters
```


# Add search attributes to temporal:
tctl namespace register default
tctl admin cluster add-search-attributes --name userId --type Keyword
tctl admin cluster add-search-attributes --name gatewayId --type Keyword
tctl admin cluster add-search-attributes --name scheduleId --type Keyword

# include
- entrypoint env var to select spring profiles


https://www.mongodb.com/docs/atlas/configure-api-access/#std-label-create-org-api-key
