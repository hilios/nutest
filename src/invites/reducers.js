import { SHOW_ERROR_MESSAGE } from './actions';

function errorMessage(message = '', action) {
  switch (action.type) {
    case SHOW_ERROR_MESSAGE:
      return action.message;
    default:
      return message
  }
}

export default {
  errorMessage
}
