/*
 * Activiti Modeler component part of the Activiti project
 * Copyright 2005-2014 Alfresco Software, Ltd. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

/*
 * Assignment
 */
var KisBpmAssignmentCtrl = [ '$scope', '$modal','$http', function($scope, $modal,$http) {
    // Config for the modal window
    var opts = {
        template:  'editor-app/configuration/properties/assignment-popup.html?version=' + Date.now(),
        scope: $scope
    };
	$scope.roles = [];
	$http({
		method: 'post',
		headers: {'Accept': 'application/json', 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
		url: '/role/sys-role/list'
	})
		.success(function(data, status, headers, config) {
			// console.log(data)
			$scope.roles = eval(data.data);
		})
		.error(function (data, status, headers, config) {
		});
    // Open the dialog
    $modal(opts);
}];

var KisBpmAssignmentPopupCtrl = [ '$scope', '$modal', function($scope, $modal) {

	$scope.formData = {};

	$scope.chooseUser = function() {
		var opts = {
            template:  'editor-app/configuration/properties/assignment-popup-popup.html?version=' + Date.now(),
            scope: $scope
        };
        // Open the dialog
        $modal(opts);
	}
	
    // Put json representing assignment on scope
    if ($scope.property.value !== undefined && $scope.property.value !== null
        && $scope.property.value.assignment !== undefined
        && $scope.property.value.assignment !== null) 
    {
        $scope.assignment = $scope.property.value.assignment;
    } else {
        $scope.assignment = {};
    }

    if ($scope.assignment.candidateUsers == undefined || $scope.assignment.candidateUsers.length == 0)
    {
    	$scope.assignment.candidateUsers = [{value: '',name:''}];
    }
    
    // Click handler for + button after enum value
    var userValueIndex = 1;
    $scope.addCandidateUserValue = function(index) {
        $scope.assignment.candidateUsers.splice(index + 1, 0, {value: 'value ' + userValueIndex++});
    };

    // Click handler for - button after enum value
    $scope.removeCandidateUserValue = function(index) {
        $scope.assignment.candidateUsers.splice(index, 1);
    };
    
    if ($scope.assignment.candidateGroups == undefined || $scope.assignment.candidateGroups.length == 0)
    {
    	$scope.assignment.candidateGroups = [{value: ''}];
    }
    
    var groupValueIndex = 1;
    $scope.addCandidateGroupValue = function(index) {
        $scope.assignment.candidateGroups.splice(index + 1, 0, {value: 'value ' + groupValueIndex++});
    };

    // Click handler for - button after enum value
    $scope.removeCandidateGroupValue = function(index) {
        $scope.assignment.candidateGroups.splice(index, 1);
    };
    
    $scope.save = function() {
        $scope.property.value = {};
        handleAssignmentInput($scope);
        $scope.property.value.assignment = $scope.assignment;

        $scope.updatePropertyInModel($scope.property);
        $scope.close();
    };

    // Close button handler
    $scope.close = function() {
    	handleAssignmentInput($scope);
    	$scope.property.mode = 'read';
    	$scope.$hide();
    };
    
    var handleAssignmentInput = function($scope) {
    	if ($scope.assignment.candidateUsers)
    	{
	    	var emptyUsers = true;
	    	var toRemoveIndexes = [];
	        for (var i = 0; i < $scope.assignment.candidateUsers.length; i++)
	        {
	        	if ($scope.assignment.candidateUsers[i].value != '')
	        	{
	        		emptyUsers = false;
	        	}
	        	else
	        	{
	        		toRemoveIndexes[toRemoveIndexes.length] = i;
	        	}
	        }
	        
	        for (var i = 0; i < toRemoveIndexes.length; i++)
	        {
	        	$scope.assignment.candidateUsers.splice(toRemoveIndexes[i], 1);
	        }
	        
	        if (emptyUsers)
	        {
	        	$scope.assignment.candidateUsers = undefined;
	        }
    	}
        
    	if ($scope.assignment.candidateGroups)
    	{
	        var emptyGroups = true;
	        var toRemoveIndexes = [];
	        for (var i = 0; i < $scope.assignment.candidateGroups.length; i++)
	        {
	        	if ($scope.assignment.candidateGroups[i].value != '')
	        	{
	        		emptyGroups = false;
	        	}
	        	else
	        	{
	        		toRemoveIndexes[toRemoveIndexes.length] = i;
	        	}
	        }
	        
	        for (var i = 0; i < toRemoveIndexes.length; i++)
	        {
	        	$scope.assignment.candidateGroups.splice(toRemoveIndexes[i], 1);
	        }
	        
	        if (emptyGroups)
	        {
	        	$scope.assignment.candidateGroups = undefined;
	        }
    	}
    };
    
	// 因新打开的界面上选定的数据要传输到当前modal中，所以使用此方式，这是angular.js中不同控制器之间传输数据的方式
    $scope.$on('chooseStr', function(event, data){
    	// console.log(data)
    	$scope.assignment.candidateUsers[0].value = data;
    });
    /*$scope.$on('choseAssigneeStr', function(event, data){
    	$scope.assignment.assignee = data;
    });
    $scope.$on('choseCandidateGroupsStr', function(event, data){
    	$scope.assignment.candidateGroups[0].value = data;
    });*/
}];
//用户选择模态框的控制器
var KisBpmChoseAssignmentCtrl = ['$scope', '$http', '$modal', function($scope, $http, $modal) {
    // Close button handler
    $scope.close = function() {
        $scope.$hide();
    };

    //Save Data
    $scope.save = function() {
		$scope.$emit('chooseStr', $scope.role);

		// console.log($scope.formData.type);
    	// if ($scope.formData.type == "dept") {
    	// 	// 子窗口向父窗口传输数据
    	// 	$scope.$emit('chooseStr', '${dept}');
		// } else {
		// 	// 子窗口向父窗口传输数据
		// 	var opts = {
	    //         template:  'editor-app/configuration/properties/assignment-user.html?version=' + Date.now(),
	    //         scope: $scope
	    //     };
	    //     // Open the dialog
	    //     $modal(opts);
		// }
        /*if ($scope.choseAssignmentFlag == "assignee") {
            var choseAssignee = $scope.formData.assignee;
            //子窗口向父窗口传输数据
            $scope.$emit('choseAssigneeStr', choseAssignee);
        } else if ($scope.choseAssignmentFlag == "assignees") {
            var choseAssignees = $scope.accounts;
            var choseAssigneesStr = "";
            for (var i=0;i<choseAssignees.length; i++) {
                if (choseAssignees[i].selected) {
                    choseAssigneesStr += choseAssignees[i].id + ",";
                }
            }
            choseAssigneesStr = choseAssigneesStr.substring(0,choseAssigneesStr.length-1);
            //子窗口向父窗口传输数据
            $scope.$emit('choseAssigneesStr', choseAssigneesStr);
        }*/
        $scope.close();
    };
}];

var KisBpmChoseUserCtrl = ['$scope', '$http', function($scope, $http) {
	$scope.users = [];
	var choose = new Array();

	$http({
		method: 'get',
		headers: {'Accept': 'application/json', 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
		url: '/user/listAll'
	})
	.success(function(data, status, headers, config) {
		// console.log(data)
		$scope.users = eval(data.data);
	})
	.error(function (data, status, headers, config) {
    });

	// Close button handler
    $scope.close = function() {
        $scope.$hide();
    };

    // $scope.save = function() {
    // 	console.log(choose.toString())
    // 	$scope.close();
	// };

	$scope.choose = function(v) {
		choose.push(v)
	}
}]
