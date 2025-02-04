package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 従業員保存
    @Transactional
    public ErrorKinds save(Report report, UserDetail userDetail) {

        List<Report> DateCheck = findByReportDateAndEmployee(report.getReportDate(), userDetail.getEmployee());
            if (DateCheck != null) {
                return ErrorKinds.DATECHECK_ERROR;
            }

        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);
        Employee loginUser=userDetail.getEmployee();
        report.setEmployee(loginUser);
        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    public List<Report> findByReportDateAndEmployee(LocalDate reportDate, Employee employee) {
        List<Report> reps = reportRepository.findByReportDateAndEmployee(reportDate, employee);
        if (reps.isEmpty()) {
            return null;
        }
        return reps;
        }

    // 従業員削除
    @Transactional
    public void delete(int id) {

        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        reportRepository.deleteById(id);
    }

    // 従業員一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 1件を検索
    public Report findById(int id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    //従業員更新
    @Transactional
    public ErrorKinds update(Report report, int id) {

        report.setDeleteFlg(false);

        Optional<Report> existingReportOpt = reportRepository.findById(report.getId());
        Report existingReport = existingReportOpt.get();  //DBから取得した日報

        if(!existingReport.getReportDate().equals(report.getReportDate())){
            //existingReport.setReportDate(report.getReportDate());
            List<Report> DateChecks = findByReportDateAndEmployee(report.getReportDate(), report.getEmployee());
            if(DateChecks != null) {
                return ErrorKinds.DATECHECK_ERROR;
            }
        }

        existingReport.setReportDate(report.getReportDate());
        existingReport.setTitle(report.getTitle());
        existingReport.setContent(report.getContent());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createTime = findById(id).getCreatedAt();
        existingReport.setCreatedAt(createTime);
        existingReport.setUpdatedAt(now);

        reportRepository.save(existingReport);

        return ErrorKinds.SUCCESS;
    }
}

