var app = angular.module('nutest', ['ngResource', 'ui.bootstrap', 'angularFileUpload']);

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
      method: 'POST'
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
  $scope.version = '*.*.*';
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

app.controller('FormController', function($scope, $uibModalInstance, FileUploader, Reward) {
  $scope.invites = undefined;
  $scope.invitesUploader = new FileUploader();

  $scope.send = function(form) {
    form.$setValidity('invites', true);

    if ($scope.invites) {
      Reward.update($scope.invites.trim(), function() {
        $uibModalInstance.close(true);
      }, function() {
        form.$setValidity('invites', false);
      });
    } else {
      // TODO: File upload
    }
  }
});
