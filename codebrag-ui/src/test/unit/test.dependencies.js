// Libraries
EnvJasmine.loadGlobal(EnvJasmine.libDir + "jquery-1.8.2-min.js");
EnvJasmine.loadGlobal(EnvJasmine.libDir + "angular-1.1.1.js");
EnvJasmine.loadGlobal(EnvJasmine.libDir + "angular-resource-1.1.1.js");
EnvJasmine.loadGlobal(EnvJasmine.libDir + "angular-sanitize-1.1.1.js");
EnvJasmine.loadGlobal(EnvJasmine.libDir + "angular-cookies-1.1.1.js");
EnvJasmine.loadGlobal(EnvJasmine.libDir + "angular-ui-states.js");
EnvJasmine.loadGlobal(EnvJasmine.libDir + "bootstrap.js");
EnvJasmine.loadGlobal(EnvJasmine.libDir + "moment.js");
EnvJasmine.loadGlobal(EnvJasmine.libDir + "lodash.js");

// Testing libraries
EnvJasmine.loadGlobal(EnvJasmine.testDir + "../lib/require/require-2.0.6.js");
EnvJasmine.loadGlobal(EnvJasmine.testDir + "require.conf.js");
EnvJasmine.loadGlobal(EnvJasmine.testDir + "../lib/angular/angular-mocks-1.1.1.js");

// Application

<!-- main module -->
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "app.js");

<!-- security -->
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "auth/httpAuthInterceptor.js");
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "auth/httpErrorsInterceptor.js");
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "auth/authService.js");
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "auth/currentUserResolver.js");
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "auth/httpRequestsBuffer.js");
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "auth/loginForm.js");

<!-- session -->
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "session/sessionCtrl.js");
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "session/profileCtrl.js");

<!-- commits -->
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "commits/commitsCtrl.js");
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "commits/commitsListService.js");
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "commits/commitsListItemCtrl.js");
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "commits/commitDetailsCtrl.js");
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "commits/comments/commentCtrl.js");
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "commits/comments/commentsResource.js");
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "commits/commitFilesService.js");

<!-- follow-ups -->
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "followups/followupsCtrl.js");
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "followups/followupListItemCtrl.js");
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "followups/followupsResource.js");

<!--commons -->
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "common/filters/dateFormatFilter.js");
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "common/directives/messagePopup.js");
