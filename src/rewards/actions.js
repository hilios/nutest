import { Record, List } from 'immutable';
import { setLoading } from '../layout/actions';

export const LIST_REWARDS = 'LIST_REWARDS';

export class Reward extends Record({customer_id: null, points: null}) {}

export function listRewards(args = {}) {
  return {type: LIST_REWARDS, ...args};
}

export function fetchRewards() {
  return (dispatch, getState) => {
    let apiUrl = getState().get('apiUrl');

    dispatch(setLoading(true));
    dispatch(listRewards());
    fetch(`${apiUrl}/rewards`)
      .then(response => response.json())
      .then(json => {
        let rewards = Object.keys(json).map(index => new Reward({
          customer_id: index, points: json[index]
        }));

        dispatch(setLoading(false));
        dispatch(listRewards({items: List(rewards)}));
      });
  };
}
