package com.fbr.rest;

import com.fbr.domain.Attribute.Attribute;
import com.fbr.domain.Company.Company;
import com.fbr.domain.Graph.Graph;
import com.fbr.domain.Question.Question;
import com.fbr.domain.Response.Response;
import com.fbr.domain.Response.ResponseList;
import com.fbr.services.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


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
    private View jsonView_i;

    private static final String DATA_FIELD = "data";
    private static final String ERROR_FIELD = "error";

    @RequestMapping(value = {"/attributes"}, method = {RequestMethod.POST})
    @ResponseBody
    public Attribute addAttribute(@RequestBody Attribute attribute,
                                  HttpServletResponse httpResponse_p, WebRequest request_p) {
        Attribute returnVal = attributeService.addAttributeAndValues(attribute);

        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/attribute/" + returnVal.getAttributeId());
        return returnVal;
    }


    @RequestMapping(value = {"/attribute/{attrId}"}, method = {RequestMethod.PUT})
    @ResponseBody
    public Attribute updateAttributeAndValues(@PathVariable("attrId") int attrId, @RequestBody Attribute attribute,
                                              HttpServletResponse httpResponse_p, WebRequest request_p) {
        Attribute dbEntry = attributeService.updateAttributeAndValues(attrId, attribute);

        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/attribute/" + dbEntry.getAttributeId());
        return dbEntry;
    }

    @RequestMapping(value = {"/attribute/{attrId}"}, method = RequestMethod.DELETE)
    @ResponseBody
    public Attribute deleteAttributeAndValues(@PathVariable("attrId") int attrId,
                                              HttpServletResponse httpResponse_p) {
        attributeService.deleteAttributeAndValues(attrId);
        httpResponse_p.setStatus(HttpStatus.OK.value());
        return null;
    }

    @RequestMapping(value = {"/attribute/{attrId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public Attribute getAttributeAndValues(@PathVariable("attrId") int attrId) {
        return attributeService.getAttributeAndValues(attrId);
    }

    @RequestMapping(value = {"/attributes"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<Attribute> getAttributesAndValues() {
        return attributeService.getAttributeAndValues();
    }


    @RequestMapping(value = {"/company/{companyId}/questions"}, method = {RequestMethod.POST})
    @ResponseBody
    public Question addQuestionsAndAnswers(@PathVariable("companyId") int companyId, @RequestBody Question question,
                                           HttpServletResponse httpResponse_p, WebRequest request_p) {
        Question returnVal = questionService.addQuestionAndAnswers(companyId, question);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/questions/company/" + companyId + "/question/" + returnVal.getQuestionId());
        return returnVal;
    }

    @RequestMapping(value = {"/company/{companyId}/question/{questionId}"}, method = {RequestMethod.PUT})
    @ResponseBody
    public Question updateQuestionAndAnswers(@PathVariable("companyId") int companyId, @PathVariable("questionId") int questionId, @RequestBody Question question,
                                             HttpServletResponse httpResponse_p, WebRequest request_p) {
        Question returnVal = questionService.updateQuestionAndAnswers(companyId, questionId, question);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/questions/company/" + companyId + "/question/" + returnVal.getQuestionId());
        return returnVal;

    }

    @RequestMapping(value = {"/company/{companyId}/question/{questionId}"}, method = RequestMethod.DELETE)
    @ResponseBody
    public Question deleteQuestionAndAnswers(@PathVariable("companyId") int companyId, @PathVariable("questionId") int questionId,
                                             HttpServletResponse httpResponse_p) {
        questionService.deleteQuestionAndAnswers(companyId, questionId);
        httpResponse_p.setStatus(HttpStatus.OK.value());
        return null;
    }

    @RequestMapping(value = {"/company/{companyId}/question/{questionId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public Question getQuestionAndAnswers(@PathVariable("companyId") int companyId, @PathVariable("questionId") int questionId) {
        return questionService.getQuestionAndAnswers(companyId, questionId);
    }

    @RequestMapping(value = {"/company/{companyId}/questions"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<Question> getQuestionsAndAnswers(@PathVariable("companyId") int companyId) {
        return questionService.getQuestionAndAnswers(companyId);
    }


    @RequestMapping(value = {"/company/{companyId}/branch/{branchId}/responses"}, method = {RequestMethod.POST})
    public Response addResponses(@PathVariable("companyId") int companyId, @PathVariable("branchId") int branchId,
                                 @RequestBody ResponseList responseList, HttpServletResponse httpResponse_p, WebRequest request_p) {
        responseService.processResponse(companyId, branchId, responseList.getResponses());
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        return null;
    }

    @RequestMapping(value = {"/companies"}, method = {RequestMethod.POST})
    public ModelAndView addCompany(@RequestBody Company company,
                                   HttpServletResponse httpResponse_p, WebRequest request_p) {
        Company returnVal;
        try {
            returnVal = companyService.addCompanyAndBranches(company);
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/company/" + returnVal.getId());
        return new ModelAndView(jsonView_i, DATA_FIELD, returnVal);
    }

    @RequestMapping(value = {"/company/{companyId}"}, method = {RequestMethod.PUT})
    public ModelAndView updateCompany(@PathVariable("companyId") int companyId, @RequestBody Company company,
                                      HttpServletResponse httpResponse_p, WebRequest request_p) {
        Company returnVal;
        try {
            returnVal = companyService.updateCompanyAndBranches(companyId, company);
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/company/" + returnVal.getId());
        return new ModelAndView(jsonView_i, DATA_FIELD, returnVal);
    }

    @RequestMapping(value = {"/companies"}, method = {RequestMethod.GET})
    public ModelAndView getCompanies() {
        try {
            return new ModelAndView(jsonView_i, DATA_FIELD, companyService.getCompanies());
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }
    }

    @RequestMapping(value = {"/company/{companyId}"}, method = {RequestMethod.GET})
    public ModelAndView getCompany(@PathVariable("companyId") int companyId) {
        try {
            return new ModelAndView(jsonView_i, DATA_FIELD, companyService.getCompany(companyId));
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }
    }

    @RequestMapping(value = {"/company/{companyId}/graphs"}, method = {RequestMethod.POST})
    public ModelAndView addGraph(@PathVariable("companyId") int companyId, @RequestBody Graph graph,
                                 HttpServletResponse httpResponse_p, WebRequest request_p) {
        Graph returnVal;
        try {
            returnVal = graphService.addGraph(companyId, graph);
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/graph/" + returnVal.getGraphId());
        return new ModelAndView(jsonView_i, DATA_FIELD, returnVal);
    }

    @RequestMapping(value = {"/graph/{graphId}"}, method = {RequestMethod.PUT})
    public ModelAndView updateGraph(@PathVariable("graphId") String graphId, @RequestBody Graph graph,
                                    HttpServletResponse httpResponse_p, WebRequest request_p) {
        Graph returnVal;
        try {
            returnVal = graphService.updateGraph(graphId, graph);
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }

        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/graph/" + graphId);
        return new ModelAndView(jsonView_i, DATA_FIELD, returnVal);
    }

    @RequestMapping(value = {"/graph/{graphId}"}, method = {RequestMethod.DELETE})
    public ModelAndView deleteGraph(@PathVariable("graphId") String graphId,
                                    HttpServletResponse httpResponse_p) {
        try {
            graphService.deleteGraph(graphId);
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }
        httpResponse_p.setStatus(HttpStatus.OK.value());
        return new ModelAndView(jsonView_i, DATA_FIELD, null);
    }

    @RequestMapping(value = {"/company/{companyId}/graphs"}, method = {RequestMethod.GET})
    public
    @ResponseBody
    List<Graph> getGraphs(@PathVariable("companyId") int companyId) {
        try {
            return graphService.getGraphs(companyId);
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            //return createErrorResponse(String.format(sMessage, e.toString()));
            return null;
        }
    }

    @RequestMapping(value = {"/graph/{graphId}"}, method = {RequestMethod.GET})
    public ModelAndView getGraph(@PathVariable("graphId") String graphId) {
        try {
            return new ModelAndView(jsonView_i, DATA_FIELD, graphService.getGraph(graphId));
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }
    }

    @RequestMapping(value = {"/company/{companyId}/refresh"}, method = {RequestMethod.PUT})
    public ModelAndView refreshCompanyData(@PathVariable("companyId") int companyId,
                                           HttpServletResponse httpResponse_p, WebRequest request_p) {
        try {
            statisticsService.resetCompanyData(companyId);
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }

        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        //httpResponse_p.setHeader("Location", request_p.getContextPath() + "/trend/" + trendId);
        return new ModelAndView(jsonView_i, DATA_FIELD, null);
    }


    private ModelAndView createErrorResponse(String sMessage) {
        return new ModelAndView(jsonView_i, ERROR_FIELD, sMessage);
    }
}
