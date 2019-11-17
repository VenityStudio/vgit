package org.venity.vgit.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.venity.vgit.exceptions.ImTeapotException;

import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/espresso")
public class EspressoAPIController extends AbstractController {

    @RequestMapping
    public String recipe() throws ImTeapotException {
        if (ThreadLocalRandom.current().nextInt(0, 11) > 5) {
            return "To make a classic espresso, take 1.5–2 teaspoons of roasted coffee to a dark color per 1 serving, place it in an espresso coffee machine.\n" +
                    "\n" +
                    "The usual serving of ready-made espresso is 40-50 ml, although its volume in a cup is at least 60 ml.";
        }

        throw new ImTeapotException();
    }
}
