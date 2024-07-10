package com.study.dvd.dao;

import com.study.dvd.db.DBConnectionMgr;
import com.study.dvd.entity.DVD;
import com.study.dvd.entity.Producer;
import com.study.dvd.entity.Publisher;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DVDDao {
    public static int addDvd(DVD dvd) {
        DBConnectionMgr pool = DBConnectionMgr.getInstance();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int successCount = 0;

        try {
            connection = pool.getConnection(); //데이터베이스 연결, db에 접근한 객체, 예외처리필요
            String sql = "insert into dvd_tb values(0, ?, ?, ?, ?, ?, now())";
            //현재 접속한 db에서 prepareStatement 객체 생성
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, dvd.getRegistrationNumber());
            preparedStatement.setString(2, dvd.getTitle());
            preparedStatement.setInt(3, dvd.getProducer().getProducerId());
            preparedStatement.setInt(4, dvd.getPublisher().getPublisherId());
            preparedStatement.setInt(5, dvd.getPublicationYear());
            successCount = preparedStatement.executeUpdate();

        } catch (Exception e) {

        } finally {
            pool.freeConnection(connection, preparedStatement);
        }

        return successCount;
    }

    //producer_tb에 Producer 객체 넣기
    public static int addProducer(Producer producer) {
        DBConnectionMgr pool = DBConnectionMgr.getInstance();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generateKeys = null;
        int successCount = 0;

        try {
            connection = pool.getConnection(); //데이터베이스 연결, db에 접근한 객체, 예외처리필요
            String sql = "insert into producer_tb values(0, ?)";
            preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS); //미완성된 쿼리 실행시킬 준비
            preparedStatement.setString(1, producer.getProducerName()); // ? 자리에 데이터 채우기
            successCount = preparedStatement.executeUpdate(); //쿼리문 실행, insert 개수
            generateKeys = preparedStatement.getGeneratedKeys(); //방금 실행한 데이터의 키를 ResultSet 형태로 가져옴
            // PreparedStatement.RETURN_GENERATED_KEYS 옵션을 안넣으면 getGeneratedKeys 에러
            generateKeys.next(); //커서를 다음행으로
            producer.setProducerId(generateKeys.getInt(1)); //첫번째 컬럼값
        } catch (Exception e) {

        } finally {
            pool.freeConnection(connection, preparedStatement, generateKeys); //객체 소멸, 데이터베이스 연결해제
        }

        return successCount;
    }

    public static int addPublisher(Publisher publisher) {
        DBConnectionMgr pool = DBConnectionMgr.getInstance();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generateKeys = null;
        int successCount = 0;

        try {
            connection = pool.getConnection(); //데이터베이스 연결, db에 접근한 객체, 예외처리필요
            String sql = "insert into publisher_tb values(0, ?)";
            preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS); //미완성된 쿼리 실행시킬 준비
            preparedStatement.setString(1, publisher.getPublisherName()); // ? 자리에 데이터 채우기
            successCount = preparedStatement.executeUpdate(); //쿼리문 실행, insert 개수
            generateKeys = preparedStatement.getGeneratedKeys(); //방금 실행한 데이터의 키를 ResultSet 형태로 가져옴
            generateKeys.next();
            publisher.setPublisherId(generateKeys.getInt(1));
        } catch (Exception e) {

        } finally {
            pool.freeConnection(connection, preparedStatement, generateKeys); //객체 소멸, 데이터베이스 연결해제
        }

        return successCount;
    }

    public static List<DVD> findAll(int count) {
        DBConnectionMgr pool = DBConnectionMgr.getInstance();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<DVD> dvdList = new ArrayList<>();
        try {
            connection = pool.getConnection();
            String sql = "select * from dvd_view limit 0, ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, count);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Producer producer = Producer.builder()
                        .producerId(resultSet.getInt(4))
                        .producerName(resultSet.getString(5))
                        .build();
                Publisher publisher = Publisher.builder()
                        .publisherId(resultSet.getInt(6))
                        .publisherName(resultSet.getString(7))
                        .build();
                DVD dvd = DVD.builder()
                        .dvdId(resultSet.getInt(1))
                        .registrationNumber(resultSet.getString(2))
                        .title(resultSet.getString(3))
                        .producerId(producer.getProducerId())
                        .producer(producer)
                        .publisherId(publisher.getPublisherId())
                        .publisher(publisher)
                        .publicationYear(resultSet.getInt(8))
                        .databaseDate(resultSet.getDate(9).toLocalDate())
                        .build();
                dvdList.add(dvd);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(connection);
        }

        return dvdList;
    }
}