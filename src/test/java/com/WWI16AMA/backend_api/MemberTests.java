package com.WWI16AMA.backend_api;

import com.WWI16AMA.backend_api.Member.*;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.TransactionSystemException;

import javax.persistence.RollbackException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MemberTests {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    OfficeRepository officeRepository;
    /*
    Sadly a little ugly. The mockMvc is not configured to use the @ControllerAdvice,
    so there is the failMvc, but that one has no possibility of persisting.
     */
    @Autowired
    private MockMvc mockMvc;
    private MockMvc failMvc;

    @Before
    public void beforeTest() {
        this.failMvc = standaloneSetup()
                .setControllerAdvice(new MemberControllerAdvice())
                .build();
    }

    @Test
    public void testRepository() {

        long found = memberRepository.count();

        saveAndGetMember();

        assertThat(memberRepository.count()).isEqualTo(found + 1);
    }

    @Test
    public void testGetMemberController() throws Exception {

        long found = memberRepository.count();
        String limit = found != 0 ? Long.toString(found) : "1337";

        this.mockMvc.perform(get("/members").param("limit", limit))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", IsCollectionWithSize.hasSize((int) found)));
    }

    @Test
    public void testPostMemberController() throws Exception {

        long found = memberRepository.count();

        Address adr = new Address(12345, "Hamburg", "Hafenstraße 5");
        Member mem = new Member("Kurt", "Krömer",
                LocalDate.of(1975, Month.DECEMBER, 2), Gender.MALE, Status.PASSIVE,
                "karl.hansen@mail.com", adr, "DE12345678901234567890", false);

        Office[] off = {new Office(Office.Title.FLUGWART), new Office(Office.Title.KASSIERER)};
        mem.setOffices(asList(off));

        this.mockMvc.perform(post("/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.marshal(mem))).andExpect(status().isOk());

        assertThat(memberRepository.count()).isEqualTo(found + 1);
    }

    @Test
    public void testPutMemberController() throws Exception {

        Member mem = saveAndGetMember();

        Address newAddr = new Address(12345, "Neustadt", "Neustraße 5");
        mem.setAddress(newAddr);
        this.mockMvc.perform(put("/members/" + mem.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.marshal(mem)))
                .andExpect(status().isNoContent());
        assertThat(mem.getAddress()).isEqualToIgnoringGivenFields(
                memberRepository.findById(mem.getId())
                        .orElseThrow(() -> new NoSuchElementException("[TEST]: member not found"))
                        .getAddress(), "id");
    }

    @Test
    public void testPutMemberControllerMalformedInput() throws Exception {

        Member mem = saveAndGetMember();

        this.failMvc.perform(put("/members/" + TestUtil.getUnusedId(memberRepository))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.marshal(mem)))
                .andExpect(status().isNotFound());
    }

    /**
     * Here ugly because we watch a cornercase. We need the mockMvc because we need Database-validation
     * but this will result in an Exception, which would only be handled in failMvc. So we handle it by ourselves.
     */
    @Test
    public void testPutMemberControllerViolatingConstraints() throws Exception {

        Member mem = saveAndGetMember();

        mem.setAddress(null);
        try {
            // this should throw an exception, because the validation of the member
            // should fail. If there is no exception, the status won't be "Bad Request"
            this.mockMvc.perform(put("/members/" + mem.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.marshal(mem)))
                    .andExpect(status().isBadRequest());
        } catch (org.springframework.web.util.NestedServletException e) {
            // It's expected behavior to have a specific exception, which should
            // fit these criteria.
            if (!(e.getCause() instanceof TransactionSystemException &&
                    e.getCause().getCause() instanceof RollbackException)) {
                throw new Exception("Es wurden andere verursachende Exceptions erwartet.\r\n" +
                        "Damit ist nicht der erwartete Fall eingetreten.");
            }
        }
    }

    @Test
    public void testDeleteMemberController() throws Exception {

        long found = memberRepository.count();
        Member mem = saveAndGetMember();
        this.mockMvc.perform(delete("/members/" + mem.getId()))
                .andExpect(status().isNoContent());

        assertThat(found).isEqualTo(memberRepository.count());
    }

    @Test
    public void testDeleteNonexistingMember() throws Exception {
        this.failMvc.perform(delete("/members/" + TestUtil.getUnusedId(memberRepository)))
                .andExpect(status().isNotFound());
    }


    private Member saveAndGetMember() {
        Address adr = new Address(68167, "Mannheim", "Hambachstraße 3");
        Member mem = new Member("Hauke", "Haien",
                LocalDate.of(1796, Month.DECEMBER, 3), Gender.MALE, Status.PASSIVE,
                "karl.hansen@mail.com", adr, "DE12345678901234567890", false);

        List<Office> off = StreamSupport.stream(officeRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        mem.setOffices(off);

        memberRepository.save(mem);
        return mem;
    }
}