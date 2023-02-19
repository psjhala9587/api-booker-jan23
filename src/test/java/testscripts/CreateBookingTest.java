package testscripts;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import constants.Status_Code;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pojo.request.createbooking.Bookingdates;
import pojo.request.createbooking.CreateBookingRequest;

//given - all input details [URI, headers, path/query parameters, payload]
//when - submit api [headerType, endpoint]
//then - validate the response

public class CreateBookingTest {
	String token;
	
	@BeforeMethod
	public void generateToken() {
		RestAssured.baseURI = "https://restful-booker.herokuapp.com";
		Response res = RestAssured.given()
				.log().all()
				.header("Content-Type", "application/json")
				.body("{\r\n" + "   "
						+ " \"username\" : \"admin\",\r\n" + "    \"password\" : \"password123\"\r\n" + "}")
				.when().post("/auth");

	/*	System.out.println(res.asPrettyString());
		*/
		Assert.assertEquals(res.statusCode(), 200);
		token = res.jsonPath().getString("token");
		System.out.println(token);
	}
	
	@Test
	public void createBookingTest() {
		Response res = RestAssured.given()
			.headers("Content-Type","application/json")
			.headers("Accept", "application/json")
			.body("{\r\n"
					+ "    \"firstname\" : \"Maulik\",\r\n"
					+ "    \"lastname\" : \"Kanani\",\r\n"
					+ "    \"totalprice\" : 123,\r\n"
					+ "    \"depositpaid\" : true,\r\n"
					+ "    \"bookingdates\" : {\r\n"
					+ "        \"checkin\" : \"2023-05-02\",\r\n"
					+ "        \"checkout\" : \"2019-01-02\"\r\n"
					+ "    },\r\n"
					+ "    \"additionalneeds\" : \"Breakfast\"\r\n"
					+ "}")
			.when()
			.post("/booking");
		
		Assert.assertEquals(res.getStatusCode(), Status_Code.OK);
	}
	
	@Test
	public void createBookingTestWithPojo() {
		Bookingdates bookingdates = new Bookingdates();
		bookingdates.setCheckin("2018-01-01");
		bookingdates.setCheckout("2019-01-01");
		
		CreateBookingRequest  payload = new CreateBookingRequest();
		payload.setFirstname("pradeep");
		payload.setLastname("jhala");
		payload.setTotalprice(123);
		payload.setDepositpaid(true);
		payload.setAdditionalneeds("breakfast");
		payload.setBookingdates(bookingdates);
		
		
		Response res=  RestAssured.given()
				.log().all()
				.header("Content-Type", "application/json")
				.body(payload)
				.when()
				.post("/booking")
				;
					
			Assert.assertEquals(res.getStatusCode(), Status_Code.OK);
			//Assert.assertTrue(Integer.valueOf(res.jsonPath().getInt("bookingid")) instanceof Integer);
			int bookingId = res.jsonPath().getInt("bookingid"); //
			System.out.println(bookingId);
			Assert.assertTrue(bookingId>0);
			
			Assert.assertEquals(res.jsonPath().getString("booking.firstname"), payload.getFirstname());
			Assert.assertEquals(res.jsonPath().getString("booking.lastname"), payload.getLastname());
			Assert.assertEquals(res.jsonPath().getInt("booking.totalprice"), payload.getTotalprice());
			Assert.assertEquals(res.jsonPath().getBoolean("booking.depositpaid"), payload.isDepositpaid());
			//Assert.assertEquals(res.jsonPath().get("booking.firstname"), payload.getBookingdates().());
	}
	
	@Test
	public void createBookingTestInPlanMode() {
		
		String payload = "{\r\n"
				+ "    \"username\" : \"admin\",\r\n"
				+ "    \"password\" : \"password123\"\r\n"
				+ "}";
		
		RequestSpecification reqSpec = RestAssured.given();
		reqSpec.baseUri("https://restful-booker.herokuapp.com/");
		reqSpec.headers("Content-Type","application/json");
		reqSpec.body(payload);
		Response res = reqSpec.post("/auth");
		
		Assert.assertEquals(res.statusCode(), 200);
		System.out.println(res.asPrettyString());
	}
	
}
