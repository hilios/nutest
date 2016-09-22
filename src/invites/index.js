import React, { Component } from 'react';
import { connect } from 'react-redux';
import { updateInvites, showErrorMessage } from './actions';
import classNames from 'classnames';

const mapStateToProps = (state) => ({
  errorMessage: state.get('errorMessage')
});

const mapDispatchToProps = (dispatch) => ({
  showErrorMessage: (msg) =>
    dispatch(showErrorMessage(msg)),
  updateInvites: (data) =>
    dispatch(updateInvites(data))
});

class Invites extends Component {
  constructor(props) {
    super(props);
    this.onSubmit = this.onSubmit.bind(this);
  }

  componentDidMount() {
    const { showErrorMessage } = this.props;
    showErrorMessage(false);
  }

  onSubmit(data) {
    const { updateInvites } = this.props;
    updateInvites(data);
  }

  render() {
    let input;
    let { errorMessage } = this.props;

    return (
      <div className="clearfix">
        <form onSubmit={(event) => {
          event.preventDefault();
          this.onSubmit(input.value);
        }}>
          <div className={classNames("form-group", {'has-danger': errorMessage})}>
            <textarea className="form-control" rows="10" ref={(ref) => input = ref}></textarea>
            {errorMessage && <div className="form-control-feedback">{errorMessage}</div>}
          </div>
          <button type="submit" className="btn btn-primary pull-xs-right">Submit</button>
        </form>
      </div>
    );
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Invites);
