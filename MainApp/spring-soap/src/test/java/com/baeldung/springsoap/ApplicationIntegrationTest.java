package com.baeldung.springsoap;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationIntegrationTest {

    @Test
    void contextLoads() {
    }

}

//
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
//public class ApplicationIntegrationTest {
//
//    private Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
//
//    @LocalServerPort private int port = 0;
//
//    @Before()
//    public void init() throws Exception {
//        marshaller.setPackagesToScan(ClassUtils.getPackageName(GetCountryRequest.class));
//        marshaller.afterPropertiesSet();
//    }
//
//    @Test
//    public void whenSendRequest_thenResponseIsNotNull() {
//        WebServiceTemplate ws = new WebServiceTemplate(marshaller);
//        GetCountryRequest request = new GetCountryRequest();
//        request.setName("Spain");
//
//        assertThat(ws.marshalSendAndReceive("http://localhost:" + port + "/ws", request)).isNotNull();
//    }
//}
