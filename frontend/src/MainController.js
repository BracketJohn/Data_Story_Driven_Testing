angular.module('gdisApp').controller('MainController', function($scope, $mdSidenav, $location) {
    $scope.message = 'Bitte wählen Sie einen Menüpunkt aus der Leiste aus!';
    $scope.imagePath = 'imgs/some_company.jpg';
    $scope.smallNav = true;

    //init
    $(document).ready(function() { $mdSidenav('small').toggle(); });
    $('#navbar_container').animate({'width':$('#md_sidebar_small').width() + 'px'}, 0);

    $scope.toggle = function() {
        $mdSidenav('small').toggle();
        $mdSidenav('big').toggle();
        $scope.smallNav = !$scope.smallNav;
        setNavSize();
    }

    $( window ).resize(function() {
        setNavSize();
    });

    function setNavSize(){
        let curNav = $scope.smallNav ? '#md_sidebar_small' : '#md_sidebar_big';
        $('#navbar_container').animate({'width':$(curNav).width() + 'px'}, 200);
    }

    $scope.openUrl = function(url, toggle) {
        if(toggle){
            $scope.toggle()
        }
        $location.path(url);
    }

    $scope.navs = [{
        'name': 'Home',
        'descr': 'Langingpage, Startpunkt in die Anwendung.',
        'icon': 'home',
        'url': '/'
    }, {
        'name': 'Import', 
        'descr': 'Importieren von Daten und Storys.',
        'icon': 'note_add',
        'url': '/import'
    }, {
        'name': 'Export', 
        'descr': 'Exportieren von fertigen Testcases.',
        'icon': 'import_export',
        'url': '/export'
    }, {
        'name': 'Manage Entities',
        'descr': 'Anlegen und verändern von Entities.',
        'icon': 'assignment',
        'url': '/entitymanagement'
    }];

})