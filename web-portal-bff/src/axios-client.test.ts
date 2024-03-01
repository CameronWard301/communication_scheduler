describe('Axios Client', () => {
  beforeEach(() => {
    jest.resetModules();
    jest.clearAllMocks()
  })

  it('should create axios client with SSL verification on', () => {
    process.env.SSL_VERIFICATION = "true";
    const client = require('./axios-client').default;
    expect(client).toBeDefined();
    expect(client.defaults.httpsAgent).toBeUndefined();
  });

  it('should create axios client with SSL verification off', () => {
    process.env.SSL_VERIFICATION = "false";
    const client = require('./axios-client').default;
    expect(client).toBeDefined()
    expect(client.defaults.httpsAgent).toBeDefined();
    expect(client.defaults.httpsAgent.options.rejectUnauthorized).toBe(false);
  });
});
