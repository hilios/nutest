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
    },
    delete: {
      method: 'DELETE'
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

  $scope.delete = function() {
    var modal = $uibModal.open({
      templateUrl: 'confirm.html',
    });
    // Delete
    modal.result.then(function() {
      Reward.delete($scope.load);
    });
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

 /**
 * Renders a HTML loader indicator that responds to $rootScope events
 * broadcasted by HTTP interceptors.
 */
app.directive('spinner', function() {
  return {
    restrict: 'E',
    templateUrl: 'spinner.html',
    link: function(scope, element) {
      scope.displayLoading = false;
      scope.displayError = false;
      // Prevent clicks at the interface
      element.on('click', function(event) {
        // Only in root node, aka overlay
        if (event.target !== this) {
          event.preventDefault();
          event.stopPropagation();
        }
      });
      // Show spiner and hide error
      scope.$on('loading:start', function() {
        scope.displayLoading = true;
        scope.displayError = false;
      });
      // Hide spiner
      scope.$on('loading:complete', function() {
        scope.displayLoading = false;
      });
      // Hide spiner and show error
      scope.$on('loading:error', function(event, rejection) {
        if (rejection && (rejection.status === 400)) {
          scope.displayLoading = false;
        } else {
          scope.displayError = true;
        }
      });
    }
  };
});

/**
 * Intercept all HTTP requests in the app and dispatches an loading event.
 * @requires $httpProvider
 */
app.config(function($httpProvider) {
  var TEMPLATE_REGEXP = /\.html$/i;
  $httpProvider.interceptors.push(function($q, $timeout, $rootScope) {
    return {
      request: function(config) {
        if (!TEMPLATE_REGEXP.test(config.url)) {
          // Defer to when function exits
          $timeout(function() {
            $rootScope.$broadcast('loading:start');
          });
        }
        return config;
      },
      response: function(response) {
        if (!TEMPLATE_REGEXP.test(response.config.url)) {
          $timeout(function() {
            $rootScope.$broadcast('loading:complete');
          });
        }
        return response;
      },
      requestError: function(rejection) {
        $timeout(function() {
          $rootScope.$broadcast('loading:error', rejection);
        });
        return $q.reject(rejection);
      },
      responseError: function(rejection) {
        $timeout(function() {
          $rootScope.$broadcast('loading:error', rejection);
        });
        return $q.reject(rejection);
      }
    };
  });
});
