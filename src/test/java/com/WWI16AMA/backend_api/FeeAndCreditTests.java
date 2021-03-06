package com.WWI16AMA.backend_api;

import com.WWI16AMA.backend_api.Credit.CreditRepository;
import com.WWI16AMA.backend_api.Fee.FeeRepository;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class FeeAndCreditTests {

    @Autowired
    FeeRepository feeRepository;

    @Autowired
    CreditRepository creditRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetFeeController() throws Exception {

        long found = feeRepository.count();

        this.mockMvc.perform(get("/fees")).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", IsCollectionWithSize.hasSize((int) found)));
    }

    @Test
    public void testGetCreditController() throws Exception {

        long found = creditRepository.count();

        this.mockMvc.perform(get("/credits")).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", IsCollectionWithSize.hasSize((int) found)));
    }


}
