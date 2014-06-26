package com.fbr.rest;

import com.fbr.domain.Attribute.Attribute;
import com.fbr.domain.Company.Company;
import com.fbr.domain.Graph.Graph;
import com.fbr.domain.Offers_Info.OffersInfo;
import com.fbr.domain.Question.Question;
import com.fbr.domain.Response.Response;
import com.fbr.domain.Response.ResponseList;
import com.fbr.domain.Statistic.AttributeLevelStatistics;
import com.fbr.services.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


@Controller
public class FeedBackController {
    private static final Logger logger = Logger.getLogger(FeedBackController.class);
    @Autowired
    private QuestionService questionService;
    @Autowired
    private AttributeService attributeService;
    @Autowired
    private ResponseService responseService;
    @Autowired
    private GraphService graphService;
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private OffersService offersService;
    @Autowired
    private View jsonView_i;

    private static final String DATA_FIELD = "data";
    private static final String ERROR_FIELD = "error";


    /* Attribute API */

    @RequestMapping(value = {"/attributes"}, method = {RequestMethod.POST})
    @ResponseBody
    public Attribute addAttribute(@RequestBody Attribute attribute,
                                  HttpServletResponse httpResponse_p, WebRequest request_p) throws Exception {
        Attribute returnVal = attributeService.addAttributeAndValues(attribute);

        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/attribute/" + returnVal.getAttributeId());
        return returnVal;
    }

    @RequestMapping(value = {"/attribute/{attrId}"}, method = {RequestMethod.PUT})
    @ResponseBody
    public Attribute updateAttributeAndValues(@PathVariable("attrId") int attrId, @RequestBody Attribute attribute,
                                              HttpServletResponse httpResponse_p, WebRequest request_p) throws Exception {
        Attribute dbEntry = attributeService.updateAttributeAndValues(attrId, attribute);

        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/attribute/" + dbEntry.getAttributeId());
        return dbEntry;
    }

    @RequestMapping(value = {"/attribute/{attrId}"}, method = RequestMethod.DELETE)
    @ResponseBody
    public Attribute deleteAttributeAndValues(@PathVariable("attrId") int attrId,
                                              HttpServletResponse httpResponse_p) throws Exception {
        attributeService.deleteAttributeAndValues(attrId);
        httpResponse_p.setStatus(HttpStatus.OK.value());
        return null;
    }

    @RequestMapping(value = {"/attribute/{attrId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public Attribute getAttributeAndValues(@PathVariable("attrId") int attrId) throws Exception {
        return attributeService.getAttributeAndValues(attrId);
    }

    @RequestMapping(value = {"/attributes"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<Attribute> getAttributesAndValues() throws Exception {
        return attributeService.getAttributeAndValues();
    }

    @RequestMapping(value = {"/company/{companyId}/attributes"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<Attribute> getAttributesAndValuesForCompany(@PathVariable("attrId") int companyId) throws Exception {
        return attributeService.getAttributesByCompany(companyId);
    }


    /* Questions API */

    @RequestMapping(value = {"/company/{companyId}/questions"}, method = {RequestMethod.POST})
    @ResponseBody
    public Question addQuestionsAndAnswers(@PathVariable("companyId") int companyId, @RequestBody Question question,
                                           HttpServletResponse httpResponse_p, WebRequest request_p) throws Exception {
        Question returnVal = questionService.addQuestionAndAnswers(companyId, question);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/questions/company/" + companyId + "/question/" + returnVal.getQuestionId());
        return returnVal;
    }

    @RequestMapping(value = {"/company/{companyId}/question/{questionId}"}, method = {RequestMethod.PUT})
    @ResponseBody
    public Question updateQuestionAndAnswers(@PathVariable("companyId") int companyId, @PathVariable("questionId") int questionId, @RequestBody Question question,
                                             HttpServletResponse httpResponse_p, WebRequest request_p) throws Exception {
        Question returnVal = questionService.updateQuestionAndAnswers(companyId, questionId, question);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/questions/company/" + companyId + "/question/" + returnVal.getQuestionId());
        return returnVal;

    }

    @RequestMapping(value = {"/company/{companyId}/question/{questionId}"}, method = RequestMethod.DELETE)
    @ResponseBody
    public Question deleteQuestionAndAnswers(@PathVariable("companyId") int companyId, @PathVariable("questionId") int questionId,
                                             HttpServletResponse httpResponse_p) throws Exception {
        questionService.deleteQuestionAndAnswers(companyId, questionId);
        httpResponse_p.setStatus(HttpStatus.OK.value());
        return null;
    }

    @RequestMapping(value = {"/company/{companyId}/question/{questionId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public Question getQuestionAndAnswers(@PathVariable("companyId") int companyId,
                                          @PathVariable("questionId") int questionId) throws Exception {
        return questionService.getQuestionAndAnswers(companyId, questionId);
    }

    @RequestMapping(value = {"/company/{companyId}/questions"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<Question> getQuestionsAndAnswers(@PathVariable("companyId") int companyId) throws Exception {
        return questionService.getQuestionAndAnswers(companyId);
    }


    /* Company API */

    @RequestMapping(value = {"/companies"}, method = {RequestMethod.POST})
    @ResponseBody
    public Company addCompany(@RequestBody Company company,
                              HttpServletResponse httpResponse_p, WebRequest request_p) throws Exception {
        Company returnVal = companyService.addCompanyAndBranches(company);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/company/" + returnVal.getId());
        return returnVal;
    }

    @RequestMapping(value = {"/company/{companyId}"}, method = {RequestMethod.PUT})
    @ResponseBody
    public Company updateCompany(@PathVariable("companyId") int companyId, @RequestBody Company company,
                                 HttpServletResponse httpResponse_p, WebRequest request_p) throws Exception {
        Company returnVal = companyService.updateCompanyAndBranches(companyId, company);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/company/" + returnVal.getId());
        return returnVal;
    }

    @RequestMapping(value = {"/company/{companyId}"}, method = {RequestMethod.DELETE})
    @ResponseBody
    public Company deleteCompany(@PathVariable("companyId") int companyId,
                                 HttpServletResponse httpResponse_p, WebRequest request_p) throws Exception {
        companyService.deleteCompanyAndBranches(companyId);
        httpResponse_p.setStatus(HttpStatus.OK.value());
        return null;
    }

    @RequestMapping(value = {"/companies"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<Company> getCompanies() throws Exception {
        return companyService.getCompanies();
    }

    @RequestMapping(value = {"/company/{companyId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public Company getCompany(@PathVariable("companyId") int companyId) throws Exception {
        return companyService.getCompany(companyId);
    }


    /* Graph API */

    @RequestMapping(value = {"/company/{companyId}/graphs"}, method = {RequestMethod.POST})
    @ResponseBody
    public Graph addGraph(@PathVariable("companyId") int companyId, @RequestBody Graph graph,
                          HttpServletResponse httpResponse_p, WebRequest request_p) throws Exception {
        Graph returnVal = graphService.addGraph(companyId, graph);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/graph/" + returnVal.getGraphId());
        return returnVal;
    }

    @RequestMapping(value = {"/company/{companyId}/graph/{graphId}"}, method = {RequestMethod.PUT})
    @ResponseBody
    public Graph updateGraph(@PathVariable("companyId") int companyId, @PathVariable("graphId") String graphId, @RequestBody Graph graph,
                             HttpServletResponse httpResponse_p, WebRequest request_p) throws Exception {
        Graph returnVal = graphService.updateGraph(companyId, graphId, graph);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/graph/" + graphId);
        return returnVal;
    }

    @RequestMapping(value = {"/company/{companyId}/graph/{graphId}"}, method = {RequestMethod.DELETE})
    @ResponseBody
    public Graph deleteGraph(@PathVariable("companyId") int companyId, @PathVariable("graphId") String graphId,
                             HttpServletResponse httpResponse_p) throws Exception {
        graphService.deleteGraph(companyId, graphId);
        httpResponse_p.setStatus(HttpStatus.OK.value());
        return null;
    }

    @RequestMapping(value = {"/company/{companyId}/graphs"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<Graph> getGraphs(@PathVariable("companyId") int companyId) throws Exception {
        return graphService.getGraphs(companyId);
    }

    @RequestMapping(value = {"/company/{companyId}/graph/{graphId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public Graph getGraph(@PathVariable("companyId") int companyId,
                          @PathVariable("graphId") String graphId) throws Exception {
        return graphService.getGraph(companyId, graphId);
    }


    /*   Offers and Info API */

    @RequestMapping(value = {"/company/{companyId}/offersAndInfo"}, method = {RequestMethod.POST})
    @ResponseBody
    public OffersInfo addOffersInfo(@PathVariable("companyId") int companyId, @RequestBody OffersInfo offersInfo,
                                    HttpServletResponse httpResponse_p) throws Exception {
        offersService.addOffersInfo(companyId, offersInfo);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        return offersInfo;
    }

    @RequestMapping(value = {"/company/{companyId}/offersAndInfo/{offerId}"}, method = {RequestMethod.PUT})
    @ResponseBody
    public OffersInfo addOffersInfo(@PathVariable("companyId") int companyId, @PathVariable("offerId") int offerId, @RequestBody OffersInfo offersInfo,
                                    HttpServletResponse httpResponse_p) throws Exception {
        offersService.updateOffersInfo(companyId, offerId, offersInfo);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        return offersInfo;
    }

    @RequestMapping(value = {"/company/{companyId}/offersAndInfo"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<OffersInfo> addOffersInfo(@PathVariable("companyId") int companyId) throws Exception {
        return offersService.getOffersInfo(companyId);
    }

    @RequestMapping(value = {"/company/{companyId}/branch/{branchId}/offersAndInfo"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<OffersInfo> addOffersInfo(@PathVariable("companyId") int companyId, @PathVariable("branchId") int branchId) throws Exception {
        return offersService.getOffersInfo(companyId, branchId);
    }

    /*  Responses API  */

    @RequestMapping(value = {"/company/{companyId}/branch/{branchId}/responses"}, method = {RequestMethod.POST})
    @ResponseBody
    public Response addResponses(@PathVariable("companyId") int companyId, @PathVariable("branchId") int branchId,
                                 @RequestBody ResponseList responseList, HttpServletResponse httpResponse_p, WebRequest request_p) throws Exception {
        responseService.processResponse(companyId, branchId, responseList.getResponses());
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        return null;
    }


    /*  Statistics API */

    @RequestMapping(value = {"/company/{companyId}/refresh"}, method = {RequestMethod.PUT})
    @ResponseBody
    public Response refreshCompanyData(@PathVariable("companyId") int companyId,
                                       HttpServletResponse httpResponse_p) throws Exception {
        statisticsService.resetCompanyData(companyId);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        return null;
    }

    @RequestMapping(value = {"/company/{companyId}/graph/{graphId}/statistics"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<AttributeLevelStatistics> getStatistics(@RequestParam(required = false) Map<String, String> reqParams,
                                                        @PathVariable("companyId") int companyId, @PathVariable("graphId") String graphId) throws Exception {
        Map<String, Integer> mapOfFilters = attributeService.getMapOfInputArgumentFilters(companyId, reqParams);
        return statisticsService.getGraphData(companyId, graphId, mapOfFilters);
    }


    /* Exception Handler*/
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(Exception ex, final HttpServletRequest httpServletRequest) {
        return new ModelAndView(jsonView_i, ERROR_FIELD, ex.getMessage());
    }
}
