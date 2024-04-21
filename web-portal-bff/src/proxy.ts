import httpProxy from "http-proxy";

export const proxy = httpProxy.createProxyServer({ws:true});
export default proxy;
