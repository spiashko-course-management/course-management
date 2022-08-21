export const getLoginUrl = () => {
  return `/oauth2/authorization/oidc`;
};

export const getLogoutUrl = () => {
  return `/logout?redirectUrl=${location.origin}`;
};

export const rememberRedirect = () => {
  localStorage.setItem(REDIRECT_URL, window.location.pathname);
};

export const REDIRECT_URL = 'redirectURL';
