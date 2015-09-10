app = angular.module("ChatApp", [])

app.controller("chatCtrl", ["$scope", ($scope)->
  $scope.msgs = []

  webScoket = new WebSocket("ws://localhost:8000")
  webScoket.onmessage = (e)->
    $scope.msgs.push(e.data)
    $scope.$apply()

  webScoket.onopen = ->
    console.log("open")

  $scope.sendMessage = ()->
    webScoket.send($scope.message)
    $scope.message = ""
])