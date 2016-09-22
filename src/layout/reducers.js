import { SET_API_URL, SET_API_VERSION, IS_LOADING } from './actions';

/**
 * Reducer functions
 */
function apiUrl(url = '', action) {
  switch (action.type) {
    case SET_API_URL:
      return action.url;
    default:
      return url;
  }
}

function apiVersion(version = 'x.x.x', action) {
  switch (action.type) {
    case SET_API_VERSION:
      return action.version;
    default:
      return version;
  }
}

function isLoading(isLoading = false, action) {
  switch (action.type) {
    case IS_LOADING:
      return action.isLoading;
    default:
      return isLoading;
  }
}

export default {
  isLoading,
  apiVersion,
  apiUrl
};
