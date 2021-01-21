package io.example.client.controller;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.IanaLinkRelations.INDEX;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/index")
public class IndexController {

    @GetMapping
    public RepresentationModel index(){
        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(IndexController.class).withRel(INDEX));
        return representationModel;
    }
}
