package com.sde.test;

import com.sde.Application;
@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("ramu6214@gmail.com")
public class AuthControllerTest {
	@RunWith(SpringRunner.class)
	@SpringBootTest
	@AutoConfigureMockMvc
	public class TokenAuthenticationServiceTest {

		@Autowired
		private MockMvc mvc;

		@Test
		public void shouldNotAllowAccessToUnauthenticatedUsers() throws Exception {
			mvc.perform(MockMvcRequestBuilders.get("/signin")).andExpect(status().isForbidden());
		}

		@Test
		public void shouldGenerateAuthToken() throws Exception {
			String token = TokenAuthenticationService.createToken("john");

			assertNotNull(token);
			mvc.perform(MockMvcRequestBuilders.get("/test").header("Authorization", token)).andExpect(status().isOk());
		}

	}
}
