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

app.controller('FormController', function($scope, $uibModalInstance, FileUploader, ApiUrl, Reward) {
  $scope.invites = "";
  $scope.invitationFile = undefined;

  $scope.send = function(form) {
    form.$setValidity('invites', true);

    if ($scope.invitationFile) {
      var data = new FormData();
      data.append('invites', $scope.invitationFile);
      // TODO: File upload
      Reward.create(data, function() {
        $uibModalInstance.close(true);
      }, function() {
        form.$setValidity('invites', false);
      });
      console.log("upload", $scope.invitationFile);

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

 /*
 var uploader = new FileUploader({url: ApiUrl + '/rewards'})

 // FILTERS
 uploader.filters.push({
     name: 'customFilter',
     fn: function(item /*{File|FileLikeObject}* /, options) {
         return this.queue.length < 10;
     }
 });

 uploader.onWhenAddingFileFailed = function(item /*{File|FileLikeObject}* /, filter, options) {
   console.info('onWhenAddingFileFailed', item, filter, options);
 };
 uploader.onAfterAddingFile = function(fileItem) {
   console.info('onAfterAddingFile', fileItem);
 };
 uploader.onAfterAddingAll = function(addedFileItems) {
   console.info('onAfterAddingAll', addedFileItems);
 };
 uploader.onBeforeUploadItem = function(item) {
   console.info('onBeforeUploadItem', item);
 };
 uploader.onProgressItem = function(fileItem, progress) {
   console.info('onProgressItem', fileItem, progress);
 };
 uploader.onProgressAll = function(progress) {
   console.info('onProgressAll', progress);
 };
 uploader.onSuccessItem = function(fileItem, response, status, headers) {
   console.info('onSuccessItem', fileItem, response, status, headers);
 };
 uploader.onErrorItem = function(fileItem, response, status, headers) {
   console.info('onErrorItem', fileItem, response, status, headers);
 };
 uploader.onCancelItem = function(fileItem, response, status, headers) {
   console.info('onCancelItem', fileItem, response, status, headers);
 };
 uploader.onCompleteItem = function(fileItem, response, status, headers) {
   console.info('onCompleteItem', fileItem, response, status, headers);
 };
 uploader.onCompleteAll = function() {
     console.info('onCompleteAll');
 };
 */
