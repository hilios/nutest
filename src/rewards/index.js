import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';

const mapStateToProps = (state) => ({
  rewards: state.get('rewards')
})

class Rewards extends Component {
  render() {
    const { rewards } = this.props;
    return (
      <div>
        {rewards.size === 0 && <p className="text-xs-center">
          <small>Empty. Try to <Link to="/invites">add some invites</Link>.</small>
        </p>}
        <ul className="list-group">
          {rewards.map(reward => (
            <li key={reward.get('customer_id')} className="list-group-item">
              <span className="tag tag-default tag-pill pull-xs-right">{reward.get('points')}</span>
              #{reward.get('customer_id')}
            </li>
          ))}
        </ul>
      </div>
    );
  }
}

export default connect(mapStateToProps)(Rewards);
