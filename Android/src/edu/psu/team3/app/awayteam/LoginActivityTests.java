package edu.psu.team3.app.awayteam;


import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;

public class LoginActivityTests extends ActivityInstrumentationTestCase2<LoginActivity>{

	private LoginActivity testActivity;

	
	public LoginActivityTests() {
        super(LoginActivity.class);
    }
	
	/**
     * Sets up the test fixture for this test case. This method is always called before every test
     * run.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        //Sets the initial touch mode for the Activity under test. This must be called before
        //getActivity()
        setActivityInitialTouchMode(true);

        //Get a reference to the Activity under test, starting it if necessary.
        testActivity = getActivity();

        //Get references to all views
    }
    
    public void testPreconditions() {
        assertNotNull("Activity is null", testActivity);;
    }
    
    //Tests///////////////////////////////
    
    public void testTrue(){
    	assertTrue(true);
    }
    
    public void testFalse(){
    	assertFalse(true);
    }

}
