import React from 'react';
import { Link } from 'react-router';
import routes from '../../routes';

export let Header = () => (
  <div>
    <header className="navbar">
      <h1 className="navbar-brand m-b-0">Nutest</h1>
      <nav className="nav nav-pills pull-xs-right">
        {routes.map(r =>
          <li className="nav-item" key={r.path}>
            <Link className="nav-link" activeClassName="active" to={r.path}>{r.title}</Link>
          </li>
        )}
      </nav>
    </header>
  </div>
);

export default Header;
