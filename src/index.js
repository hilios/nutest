import App from './app';
import routes from './routes';
import Layout from './layout';

import RewardsReducers from './rewards/reducers';
import InvitesReducers from './invites/actions';
import LayoutReducers from './layout/actions';

export const reducers = {
  ...LayoutReducers,
  ...RewardsReducers,
  ...InvitesReducers
};

export const initialState = {
  isLoading: false,
  apiUrl: 'https://nutest.herokuapp.com'
};

App({ reducers, initialState, Layout, routes }).render();
