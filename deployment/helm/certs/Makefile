KeyStoreApps = preferences-api schedule-api gateway-api sms-gateway email-gateway mock-gateway history-api auth-api

.PHONY: all $(KeyStoreApps)

all: authApi webPortal bff $(KeyStoreApps)


authApi:
	@echo "Generating public and private keys for auth API"
	openssl genrsa -out ./auth-private.key 4096
	openssl rsa -in auth-private.key -pubout -outform PEM | awk 'NF {sub(/\\r/, ""); printf "%s ",$$0;}' > auth-public-key.txt
	openssl pkcs8 -topk8 -inform PEM -in auth-private.key -nocrypt | awk 'NF {sub(/\\r/, ""); printf "%s ",$$0;}' > auth-private-key.txt

webPortal:
	@echo "Generating public and private keys for web portal"
	openssl req -x509 -nodes -days 365 -subj "//C=UK\L=Rugby\OU=ID\O=UOL\CN=www.example.com" -newkey rsa:2048 -keyout ./web-portal-private.key -out web-portal-public.crt; \
    awk 'NF {sub(/\\r/, ""); printf "%s\\\\n",$$0;}' web-portal-public.crt | sed -r 's/\\\\n$$//g'  > web-portal-public.crt.txt; \
    awk 'NF {sub(/\\r/, ""); printf "%s\\\\n",$$0;}' web-portal-private.key | sed -r 's/\\\\n$$//g'  > web-portal-private.key.txt

bff:
	@echo "Generating public and private keys for bff"
	openssl req -x509 -nodes -days 365 -subj "//C=UK\L=Rugby\OU=ID\O=UOL\CN=www.example.com" -newkey rsa:2048 -keyout ./bff-private.key -out bff-public.crt; \
    awk 'NF {sub(/\\r/, ""); printf "%s\\\\n",$$0;}' bff-public.crt | sed -r 's/\\\\n$$//g'  > bff-public.crt.txt; \
    awk 'NF {sub(/\\r/, ""); printf "%s\\\\n",$$0;}' bff-private.key | sed -r 's/\\\\n$$//g'  > bff-private.key.txt


$(KeyStoreApps):
	@echo "Generating keystore for $@"
	@read -p "Enter password for $@: " password; \
	keytool -genkeypair -noprompt -dname "CN=www.example.com, OU=ID, O=UOL, L=Rugby, S=W, C=UK" -alias $@ -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore $@.p12 -validity 3650 -storepass $$password
	base64 $@.p12 | tr -d '\n' > $@-ks-base64.txt
