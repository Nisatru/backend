package com.WWI16AMA.backend_api.Plane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;


@RestController
@RequestMapping(path = "planes")
public class PlaneController {

    @Autowired
    private PlaneRepository planeRepository;

    @GetMapping(value = "")
    public Iterable<Plane> getAllPlanesPaged() throws IllegalArgumentException {

        return planeRepository.findAll();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Plane> detail(@PathVariable int id) {

        return new ResponseEntity<>(planeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Plane with the id " + id + " does not exist")), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('VORSTANDSVORSITZENDER', 'SYSTEMADMINISTRATOR')")
    @PostMapping(path = "")
    public Plane create(@RequestBody Plane reqPlane) {

        if (reqPlane.getId() != null) {
            throw new IllegalArgumentException("Plane has the ID: " + reqPlane.getId() +
                    ". Id has to be null when a new plane shall be created");
        }

        planeRepository.save(reqPlane);

        return reqPlane;
    }

    @PreAuthorize("hasAnyRole('VORSTANDSVORSITZENDER', 'SYSTEMADMINISTRATOR')")
    @PutMapping(path = "/{id}")
    public ResponseEntity<Plane> put(@RequestBody Plane putPlane, @PathVariable int id) {

        if (planeRepository.existsById(id)) {
            putPlane.setId(id);
            planeRepository.save(putPlane);
        } else {
            throw new NoSuchElementException("Plane with id " + id + " does not exist.");
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('VORSTANDSVORSITZENDER', 'SYSTEMADMINISTRATOR')")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Plane> delete(@PathVariable int id) {

        Plane plane = planeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Plane with the id " + id + " does not exist"));
        planeRepository.delete(plane);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
