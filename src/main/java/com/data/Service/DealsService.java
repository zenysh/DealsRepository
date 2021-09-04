package com.data.Service;

import com.data.Model.Deals;
import com.data.Repository.DealPagingRepository;
import com.data.Repository.DealRepository;
import com.data.helper.ExcelHelper;
import com.data.helper.ExcelWriter;
import com.data.response.DealResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Service
public class DealsService {
    DealRepository dealRepository;
    ExcelHelper excelHelper;
    DealPagingRepository dealPagingRepository;
    ExcelWriter excelWriter;

    @Autowired
    DealsService(DealRepository dealRepository, ExcelHelper excelHelper,
                 DealPagingRepository dealPagingRepository, ExcelWriter excelWriter) {
        this.dealRepository = dealRepository;
        this.excelHelper = excelHelper;
        this.dealPagingRepository = dealPagingRepository;
        this.excelWriter = excelWriter;
    }

    @Transactional
    public boolean insertDeal(MultipartFile multipartFile) throws IOException {
        System.out.println("I reached here");
        excelHelper.getDeals(multipartFile).forEach(u -> {
            System.out.println(u.getDealid());
            System.out.println(u.getFromISOCODE());
            Deals deals = new Deals();
            deals.setDealid(u.getDealid());
            deals.setFromISOCODE(u.getFromISOCODE());
            deals.setToISOCODE(u.getToISOCODE());
            deals.setDealtime(u.getDealtime());
            deals.setDealamount(u.getDealamount());
            dealRepository.save(deals);
/*            System.out.println(u.getId());
            System.out.println(u.getFromISOCODE());
            System.out.println(u.getToISOCODE());
            System.out.println(u.getDealtime());
            System.out.println(u.getDealamount());*/
        });
        return true;
    }

    public List<DealResponse> getProductWithPage(Integer pageNo, Integer pageSize, String sortBy, String Order,
                                                 String fromdate, String todate) {
        List<DealResponse> dealResponses = new LinkedList<>();
        Pageable paging;
        if (Order.equals("asc")) {
            paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
        } else if (Order.equals("desc")) {
            paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        } else {
            paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        }
        if (!(fromdate.isEmpty() &&todate.isEmpty())) {
            List<Deals> Dealsresult = dealPagingRepository.findAllByDealtimeBetween(fromdate, todate, paging);
            Dealsresult.forEach(u -> {
                dealResponses.add(dealResponse(u));
            });
            return dealResponses;
        } else {
            Page<Deals> Dealsresult = dealPagingRepository.findAll(paging);
            if (Dealsresult.hasContent()) {
                Dealsresult.getContent().forEach(u -> {
                    dealResponses.add(dealResponse(u));
                });
                return dealResponses;
            } else {
                return new LinkedList<>();
            }
        }
    }

    private DealResponse dealResponse(Deals deals) {
        DealResponse dr = new DealResponse();
        dr.setId(deals.getId());
        dr.setDealid(deals.getDealid());
        dr.setDealamount(deals.getDealamount());
        dr.setFromISOCODE(deals.getFromISOCODE());
        dr.setToISOCODE(deals.getToISOCODE());
        dr.setDealtime(deals.getDealtime());
        return dr;
    }

    public ByteArrayInputStream loadFile(List<DealResponse> dealResponseList) {
        List deals = dealRepository.findAll();
        ByteArrayInputStream in;
        try {
            if(dealResponseList==null) {
                 in = excelWriter.dealsToExcel(deals);
            }else{
                 in = excelWriter.dealsToExcel(convertReponseToDeal(dealResponseList));
            }
            return in;
        } catch (IOException e) {
        }
        return null;
    }
    public List<Deals>convertReponseToDeal(List<DealResponse> dealResponseList){
        List<Deals> d= new LinkedList<>();
        dealResponseList.forEach(u->{
            Deals deal = new Deals();
            deal.setId(u.getId());
            deal.setDealid(u.getDealid());
            deal.setFromISOCODE(u.getFromISOCODE());
            deal.setToISOCODE(u.getToISOCODE());
            deal.setDealamount(u.getDealamount());
            deal.setDealtime(u.getDealtime());
            d.add(deal);
        });
        return d;
    }
}
