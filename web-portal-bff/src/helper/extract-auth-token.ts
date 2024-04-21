export const extractAuthToken = (token: string | undefined) => {
  if (!token) {
    return {};
  }
  return {
    Authorization: token
  };

};

export default extractAuthToken;
