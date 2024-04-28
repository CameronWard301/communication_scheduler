# Generating Certificates
To generate self-signed certificates for the Helm chart, you can use the instructions the Makefile and the instructions below

## Pre-requisites
1. [OpenSSL](https://www.openssl.org/)
2. [Make](https://www.gnu.org/software/make/)
3. [Java SDK](https://www.oracle.com/java/technologies/downloads/#java21) (for generating the keystore)
4. A UNIX based system (Linux, macOS, WSL) to run the Makefile

## Using the Makefile
1. Edit the makefile to set the correct values for the certificate generation
    1. Modify lines 16 and 30 to set the certificate details to apply to each certificate.
2. Run `make` to generate the certificates, they will be placed in the `certs` directory
    1. The Keystore generation will ask you to set a password for the keystore, remember this password for use in the Helm chart
3. See the [helm chart documentation](../README.md) for how to use the certificates in the Helm chart

