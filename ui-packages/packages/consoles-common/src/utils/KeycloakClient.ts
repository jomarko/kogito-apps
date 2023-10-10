import axios, { AxiosRequestConfig } from 'axios';
import {
  ANONYMOUS_USER,
  User,
  UserContext,
  KeycloakUserContext
} from '../environment/auth';
import Keycloak from 'keycloak-js';

export const isAuthEnabled = (): boolean => {
  return process.env.KOGITO_ENV_MODE !== 'DEV';
};

export const isKeycloakHealthCheckDisabled = (): boolean => {
  return window['KOGITO_CONSOLES_KEYCLOAK_DISABLE_HEALTH_CHECK'];
};

export const getUpdateTokenValidity = (): number => {
  const updateTokenValidity =
    window['KOGITO_CONSOLES_KEYCLOAK_UPDATE_TOKEN_VALIDITY'];
  if (typeof updateTokenValidity !== 'number') {
    return 30;
  }
  return updateTokenValidity;
};

let currentSecurityContext: UserContext;
let keycloak: Keycloak.KeycloakInstance;
export const getLoadedSecurityContext = (): UserContext => {
  /* istanbul ignore if */
  if (!currentSecurityContext) {
    /* istanbul ignore if */
    if (isAuthEnabled()) {
      throw Error(
        'Cannot load security context! Please reload screen and log in again.'
      );
    }
    currentSecurityContext = getNonAuthUserContext();
  }
  return currentSecurityContext;
};

export const checkAuthServerHealth = () => {
  return new Promise<void>((resolve, reject) => {
    fetch(window['KOGITO_CONSOLES_KEYCLOAK_HEALTH_CHECK_URL'])
      .then((response) => {
        /* istanbul ignore else */
        if (response.status === 200) {
          resolve();
        }
      })
      .catch(() => {
        reject();
      });
  });
};

export const getKeycloakClient = (): Keycloak.KeycloakInstance => {
  return Keycloak({
    realm: window['KOGITO_CONSOLES_KEYCLOAK_REALM'],
    url: window['KOGITO_CONSOLES_KEYCLOAK_URL'],
    clientId: window['KOGITO_CONSOLES_KEYCLOAK_CLIENT_ID']
  });
};

export const initializeKeycloak = (
  onloadSuccess: () => void
): Promise<void> => {
  keycloak = getKeycloakClient();
  return keycloak
    .init({
      onLoad: 'login-required'
    })
    .then((authenticated) => {
      /* istanbul ignore else */
      if (authenticated) {
        currentSecurityContext = new KeycloakUserContext({
          userName: keycloak.tokenParsed['preferred_username'],
          roles: keycloak.tokenParsed['groups'],
          token: keycloak.token,
          tokenMinValidity: getUpdateTokenValidity(),
          logout: () => handleLogout()
        });
        onloadSuccess();
      }
    });
};

export const loadSecurityContext = (
  onloadSuccess: () => void,
  onLoadFailure: () => void
): Promise<void> => {
  if (isAuthEnabled()) {
    if (isKeycloakHealthCheckDisabled()) {
      return initializeKeycloak(onloadSuccess);
    } else {
      return checkAuthServerHealth()
        .then(() => {
          return initializeKeycloak(onloadSuccess);
        })
        .catch((error) => {
          onLoadFailure();
        });
    }
  } else {
    currentSecurityContext = getNonAuthUserContext();
    onloadSuccess();
    return Promise.resolve();
  }
};

const getNonAuthUserContext = (): UserContext => {
  return {
    getCurrentUser(): User {
      return ANONYMOUS_USER;
    }
  };
};
export const getToken = (): string => {
  if (isAuthEnabled()) {
    const ctx = getLoadedSecurityContext() as KeycloakUserContext;
    return ctx.getToken();
  }
};

export const updateKeycloakToken = (): Promise<void> => {
  if (!isAuthEnabled()) {
    return;
  }
  return new Promise((resolve, reject) => {
    const ctx = getLoadedSecurityContext() as KeycloakUserContext;
    keycloak
      .updateToken(getUpdateTokenValidity())
      .then(() => {
        ctx.setToken(keycloak.token);
        resolve();
      })
      .catch((error) => {
        reject(error);
      });
  });
};

export const setBearerToken = (
  config: AxiosRequestConfig
): Promise<AxiosRequestConfig> => {
  if (!isAuthEnabled()) {
    return Promise.resolve(config);
  }
  return new Promise<AxiosRequestConfig>((resolve, reject) => {
    updateKeycloakToken()
      .then(() => {
        config.headers.Authorization = 'Bearer ' + keycloak.token;
        resolve(config);
      })
      .catch((error) => reject(error));
  });
};

export const appRenderWithAxiosInterceptorConfig = async (
  appRender: (ctx: UserContext) => void,
  onLoadFailure: () => void
): Promise<void> => {
  await loadSecurityContext(() => {
    appRender(getLoadedSecurityContext());
  }, onLoadFailure);
  if (isAuthEnabled()) {
    axios.interceptors.response.use(
      (response) => response,
      (error) => {
        /* istanbul ignore else */
        if (error.response.status === 401) {
          // if token expired - log the user out
          handleLogout();
        }
        return Promise.reject(error);
      }
    );
    axios.interceptors.request.use(
      (config) => setBearerToken(config),
      (error) => {
        /* tslint:disable:no-floating-promises */
        Promise.reject(error);
        /* tslint:enable:no-floating-promises */
      }
    );
  }
};

export const handleLogout = (): void => {
  currentSecurityContext = undefined;
  /* istanbul ignore else */
  if (keycloak) {
    keycloak.logout();
  }
};
