# Deployment

This section describes the steps to deploy the cloud resources to AWS, connect to the EKS cluster and deploy the
services.

## First Time Setup

1. Deploy the infrastructure using the provided [Terragrunt project](terragrunt)
    1. After deploying use the following instructions to connect and finish configuring the EKS cluster:
        1. Add the cluster to your local
           kubeconfig: `aws eks update-kubeconfig --region eu-west-1 --name communication-eks --profile saml`
            1. Replace the region with the region the EKS cluster is deployed in if needed.
            2. Ensure you have a profile called `saml` in your `~/.aws/credentials` file. (
               See [Terragrunt project](terragrunt) for the pre-requisites)
    2. Monitor the deployment in the AWS console or using a Kubernetes dashboard such as [Lens](https://k8slens.dev/).
        1. Wait for all the nodes to come online and core pods to be running.
    3. Enable IP prefixing to allow more pods per
       node: `kubectl set env daemonset aws-node -n kube-system ENABLE_PREFIX_DELEGATION=true`
    4. The nodes must now be restarted to apply the change.
        1. Follow these instructions to set both spot and on demand node groups desired size to
           zero: https://docs.aws.amazon.com/eks/latest/userguide/update-managed-node-group.html#mng-edit
        2. Wait for the nodes to be terminated (can take up to 10 minutes)
        3. Use the same process as step 1.iv.a to update the desired size back to the original value or apply
           the [Terraform project](terraform) again.
        4. Wait for the new nodes to come online.
2. Configure and deploy the resources to the Kubernetes cluster using the provided [Helm chart](helm)
    1. Use a tool like [Lens](https://k8slens.dev/) to monitor the deployment.
    2. Once deployed, exec into the `temporal-admintools` pod and run the following commands to create the required
       search attributes:
        1. `tctl namespace register default` (It may already exist, so this command may fail, that's fine.)
        2. `tctl admin cluster add-search-attributes --name userId --type Keyword`
        3. `tctl admin cluster add-search-attributes --name gatewayId --type Keyword`
        4. `tctl admin cluster add-search-attributes --name scheduleId --type Keyword`
        5. One created, connected clients such as the schedule API take a few minutes to register the new search
           attributes.
            1. You can force the registration by restarting the schedule and history API deployments.

