import React, { Component } from 'react';
import { connect } from 'react-redux';
import { setApiUrl, fetchVersion } from './actions'
import { deleteInvites } from '../invites/actions';
import Header from './components/header';
import Footer from './components/footer';

import './index.scss';

const mapStateToProps = state => {
  return {
    isLoading: state.get('isLoading'),
    version: state.get('apiVersion'),
    url: state.get('apiUrl')
  };
};

const mapDispatchToProps = dispatch => ({
  setUrl: (url) => {
    dispatch(setApiUrl(url));
    dispatch(fetchVersion());
  },
  fetchVersion: () =>
    dispatch(fetchVersion()),
  deleteInvites: () =>
    dispatch(deleteInvites())
});

class Layout extends Component {
  componentDidMount() {
    let { fetchVersion } = this.props;
    fetchVersion();
  }

  render() {
    let { url, version, isLoading, setUrl, deleteInvites, children } = this.props;
    return (
      <div className="container">
        <Header />
        {isLoading && <div className="spinner-wrapper"><div className="spinner" /></div>}
        <main>{children}</main>
        <Footer apiUrl={url} apiVersion={version} onChangeUrl={url => setUrl(url)} onClickDelete={() => deleteInvites()} />
      </div>
    );
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Layout);
