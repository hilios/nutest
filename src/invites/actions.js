import { browserHistory } from 'react-router';
import { setLoading } from '../layout/actions';
import { fetchRewards } from '../rewards/actions';

export const SHOW_ERROR_MESSAGE = 'SHOW_ERROR_MESSAGE';

export function showErrorMessage(message) {
  return {type: SHOW_ERROR_MESSAGE, message};
}

export function updateInvites(invites) {
  return (dispatch, getState) => {
    const apiUrl = getState().get('apiUrl');

    dispatch(setLoading(true));

    fetch(`${apiUrl}/rewards`, {
      method: 'PUT',
      body: invites
    })
    .then((response) => {
      if (response.ok) {
        dispatch(fetchRewards());
        browserHistory.push('/');
      } else {
        dispatch(setLoading(false));
        dispatch(showErrorMessage('Sorry, your form has errors!'));
      }
    });
  };
}

export function deleteInvites() {
  return (dispatch, getState) => {
    const apiUrl = getState().get('apiUrl');

    dispatch(setLoading(true));

    fetch(`${apiUrl}/rewards`, {
      method: 'DELETE'
    }).then(() => {
      dispatch(fetchRewards());
      browserHistory.push('/');
    });
  }
}
