import { List } from 'immutable';
import { LIST_REWARDS } from './actions';

function rewards(rewards = List(), action) {
  switch (action.type) {
    case LIST_REWARDS:
      return action.items || rewards;
    default:
      return rewards;
  }
}

export default {
  rewards
};
