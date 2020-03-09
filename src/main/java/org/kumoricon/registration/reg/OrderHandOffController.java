package org.kumoricon.registration.reg;

import org.kumoricon.registration.model.order.OrderHandOffDTO;
import org.kumoricon.registration.model.order.OrderHandOffRepository;
import org.kumoricon.registration.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.OffsetDateTime;

@Controller
public class OrderHandOffController {
    private static final Logger log = LoggerFactory.getLogger(OrderHandOffController.class);
    private final OrderHandOffRepository orderHandOffRepository;
    private static final String STAGE_ORDER = "order";
    private static final String STAGE_PRINT_BADGE = "printbadge";

    public OrderHandOffController(OrderHandOffRepository orderHandOffRepository) {
        this.orderHandOffRepository = orderHandOffRepository;
    }

    @RequestMapping(value = "/reg/atconorder/{orderId}/handoff",method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String atConOrderHandOff(@AuthenticationPrincipal User user,
                                    @PathVariable Integer orderId) {
        log.info("flagged order {} for handoff", orderId);

        OrderHandOffDTO o = new OrderHandOffDTO(orderId, user.getId(), user.getUsername(), OffsetDateTime.now(), STAGE_ORDER);
        orderHandOffRepository.saveHandOff(o);
        return "redirect:/?msg=Flagged+order+for+hand+off";
    }

    @RequestMapping(value = "/reg/atconorder/{orderId}/printbadges/handoff" ,method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String atConOrderHandOffBadge(@AuthenticationPrincipal User user,
                                         @PathVariable Integer orderId) {
        log.info("flagged order {} for handoff at badge print", orderId);

        OrderHandOffDTO o = new OrderHandOffDTO(orderId, user.getId(), user.getUsername(), OffsetDateTime.now(), STAGE_PRINT_BADGE);
        orderHandOffRepository.saveHandOff(o);
        return "redirect:/?msg=Flagged+order+for+hand+off";
    }


    @RequestMapping(value = "/reg/atconorder/{orderId}/takeover",method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String atConOrderTakeOver(Model model, @PathVariable Integer orderId) {
        log.info("took over order id {}", orderId);
        OrderHandOffDTO handOff = orderHandOffRepository.findByOrderId(orderId);
        String redirect;
        if (STAGE_ORDER.equals(handOff.getStage())) {
            redirect = "redirect:/reg/atconorder/" + orderId;
        } else if (STAGE_PRINT_BADGE.equals(handOff.getStage())) {
            redirect = "redirect:/reg/atconorder/" + orderId + "/printbadges";
        } else {
            log.error("Unknown order stage {}", handOff.getStage());
            throw new RuntimeException("Unknown order stage");
        }
        orderHandOffRepository.deleteHandOff(orderId);
        return redirect;
    }

    @RequestMapping(value = "/reg/atconorder/takeover")
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String atConOrderHandOffList(Model model) {
        model.addAttribute("handoffs", orderHandOffRepository.findAll());
        return "reg/atcon-order-handoff";
    }

}

