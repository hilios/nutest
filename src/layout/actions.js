import { fetchRewards } from '../rewards/actions';

export const SET_API_URL = 'SET_API_URL';
export const SET_API_VERSION = 'SET_API_VERSION';
export const IS_LOADING = 'LOADING';

/**
 * Actions
 */
export function setApiUrl(url) {
  return {type: SET_API_URL, url};
}

export function setApiVersion(version) {
  return {type: SET_API_VERSION, version};
}

export function setLoading(isLoading) {
  return {type: IS_LOADING, isLoading};
}

export function fetchVersion() {
  return (dispatch, getState) => {
    const apiUrl = getState().get('apiUrl');

    dispatch(setLoading(true));
    fetch(`${apiUrl}`)
      .then(response => response.json())
      .then(json => {
        dispatch(setApiVersion(json.version));
        dispatch(fetchRewards());
      })
      .catch(() => {
        dispatch(setLoading(false));
        dispatch(setApiVersion(''));
      });
  };
}
