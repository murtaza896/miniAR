package com.example.simplerestapis.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.simplerestapis.models.SalesforceOrg;


@Repository
public interface SalesforceOrgRepository extends JpaRepository<SalesforceOrg, String> {

//	ArrayList<SalesforceOrg> findByUserId(String user_id);
//	SalesforceOrg findById(String org_id);
}
