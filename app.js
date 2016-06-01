var app = angular.module('nutest', ['ngResource', 'ui.bootstrap']);

app.value('ApiUrl', 'https://nutest.herokuapp.com');

/**
 * Models
 */
app.factory('Status', function($resource, ApiUrl) {
  return $resource(ApiUrl + '/')
});

app.factory('Reward', function($resource, ApiUrl) {
  return $resource(ApiUrl + '/rewards', {}, {
    create: {
      method: 'POST'
    },
    update: {
      method: 'PUT'
    }
  })
});

/**
 * Controllers
 */
app.controller('StatusController', function($scope, Status) {
  $scope.version = "0.0.0"

  Status.get(function(data) {
    $scope.version = data.version;
  });
});

app.controller('RewardController', function($scope, $uibModal, Reward) {
  $scope.list = {};

  $scope.load = function() {
    Reward.get(function(data) {
      $scope.list = data;
    });
  }
  $scope.load();

  $scope.open = function(template) {
    var modal = $uibModal.open({
      templateUrl: template + '.html',
      controller: 'FormController'
    });

    modal.result.then($scope.load);
  }
});

app.controller('FormController', function($scope, $uibModalInstance, FormFactory) {
  $scope.send = function(form) {
    var handler = FormFactory.create(form);
    $uibModalInstance.close(true);
  }
});

/**
 * Others
 */
app.factory('FormFactory', function($parse, $log) {
  /**
   * Returns a FormHandler object for the given form.
   * @param {FormController} form The form instance
   */
  function FormHandler(form) {
    /**
     * Parse the errors provided from the response to the correct field.
     * @param {Object} response The HTTP promise response.
     */
    this.parseErrors = function(response) {
      angular.forEach(response.data.errors || {}, function(errors, field) {
        angular.forEach(errors, function(message) {
          try {
            var fieldPath = field.replace(/\[(\d+)\]/g, '_$1');
            $parse(fieldPath)(form).$setValidity(message, false);
          } catch (e) {
            $log.warn('Field ' + field + ' was not found');
          }
        });
      });
    };

    /**
     * Clear all errors from the given form. Navigate recursively from all sub
     * forms and fields to set the validity.
     */
    this.clearErrors = function() {
      /**
       * Iterates through all fields and sub-fields with errors and resets their
       * validity.
       * @param {Object} form The form instance
       */
      function _clearAll(form) {
        angular.forEach(form.$error, function(fields, message) {
          angular.forEach(fields, function(field) {
            if (field.$setSubmitted) {
              // Parse nested forms
              _clearAll(field);
            } else {
              field.$setValidity(message, true);
            }
          });
        });
      }
      // Start the recursive reset
      _clearAll(form);
    };
    // Start cleaning all errors.
    this.clearErrors();
  }

  return {
    /**
     * Returns a new instance of the FormHandler.
     * @param {Object} form The form instance
     * @return {FormHandler} The FormHandler instance for the given form
     */
    create: function NewFormHandler(form) {
      return new FormHandler(form);
    }
  };
});
