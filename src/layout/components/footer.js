import React, { PropTypes } from 'react';
import { Debounce } from 'react-throttle';
import classNames from 'classnames';

export const Footer = ({ apiUrl, apiVersion, onChangeUrl, onClickDelete }) => (
  <footer>
    {onClickDelete && <button type="button" className="btn btn-secondary btn-sm"
      onClick={() => {
        let ok = confirm('This action cannot be undone.\nDo you want to continue?');
        if (ok) onClickDelete();
      }}>
      Remove all
    </button>}
    <form className="form-inline pull-xs-right">
      <div className={classNames("form-group", {'has-danger': apiVersion === ''})}>
        <div className="input-group input-group-sm">
          <Debounce time="1000" handler="onChange">
            <input type="text" className="form-control" defaultValue={apiUrl} onChange={(event) =>
              onChangeUrl(event.target.value)
            } />
          </Debounce>
          <div className="input-group-addon">v{apiVersion}</div>
        </div>
      </div>
    </form>
  </footer>
)

Footer.propTypes = {
  apiUrl: PropTypes.string.isRequired,
  apiVersion: PropTypes.string.isRequired,
  onChangeUrl: PropTypes.func.isRequired,
  onClickDelete: PropTypes.func
};

export default Footer;
