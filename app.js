var app = angular.module('nutest', ['ngResource', 'ui.bootstrap']);

/**
 * Services
 */
app.constant('ApiUrl', 'https://nutest.herokuapp.com');

/**
 * Models
 */
app.factory('Status', function($resource, ApiUrl) {
  return $resource(ApiUrl + '/');
});

app.factory('Reward', function($resource, ApiUrl) {
  return $resource(ApiUrl + '/rewards', {}, {
    create: {
      method: 'POST',
      transformRequest: angular.identity,
      headers: {'Content-Type': undefined}
    },
    update: {
      method: 'PUT'
    }
  });
});

/**
 * Controllers
 */
app.controller('StatusController', function($scope, Status, ApiUrl) {
  $scope.version = '0.0.0';
  $scope.url = ApiUrl;

  Status.get(function(data) {
    $scope.version = data.version;
  });
});

app.controller('RewardController', function($scope, $uibModal, Reward) {
  $scope.rewards = {};

  $scope.load = function() {
    Reward.get(function(data) {
      $scope.rewards = data;
    });
  }
  $scope.load();

  $scope.open = function(template) {
    var modal = $uibModal.open({
      templateUrl: template + '.html',
      controller: 'FormController'
    });
    // Reload when closed
    modal.result.then($scope.load);
  }
});

app.controller('FormController', function($scope, $uibModalInstance, Reward) {
  $scope.invites = "";
  $scope.invitationFile = undefined;

  $scope.send = function(form) {
    form.$setValidity('invites', true);

    if ($scope.invitationFile) {
      var data = new FormData();
      data.append('invites', $scope.invitationFile);
      // File upload
      Reward.create(data, function() {
        $uibModalInstance.close(true);
      }, function() {
        form.$setValidity('invites', false);
      });
    } else {
      Reward.update($scope.invites.trim(), function() {
        $uibModalInstance.close(true);
      }, function() {
        form.$setValidity('invites', false);
      });
    }
  }
});

/**
 * Directives
 */
app.directive('fileInput', function() {
  return {
    restrict: 'A',
    require: '?ngModel',
    link: function(scope, element, attrs, ngModel) {
      if (!ngModel || attrs.type !== 'file') return;

      element.bind('change', function(event) {
        if (!this.files || !this.files[0]) return;
        ngModel.$setViewValue(this.files[0]);
      });
    }
   };
 });
