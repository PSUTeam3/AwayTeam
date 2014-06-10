QUnit.module("User Tests");

QUnit.asyncTest("Test authentication", function(assert) {
	QUnit.expect( 1 );
	var params = {loginId:"parkerc71",password:"tigers71"};
	authenticate(params, function(data){
		QUnit.assert.ok(true, "Authentication Passes!");
		QUnit.start();
	}, function(){
		QUnit.assert.ok(false, "Authentication Failed!");
		QUnit.start();
	});
});