package com.revature.services;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.revature.models.Reservation;
import com.revature.models.ReservationEmail;
import com.revature.models.Resource;
import com.revature.repositories.ReservationRepository;

/**
 * The Class ReservationService.
 * @author 1811-Java-Nick 12/27/18
 */
@Service
public class ReservationService {

	/** The reservation repository. */
	ReservationRepository reservationRepository;
	/** Using the user service to access the user repository*/
	UserService userService;
	/** The time now. */
	static LocalDateTime timeNow = LocalDateTime.now();
	
	/**
	 * Instantiates a new reservation service.
	 *
	 * @param reservationRepository the reservation repository
	 */
	@Autowired
	public ReservationService(ReservationRepository reservationRepository, UserService userService) {
		super();
		this.reservationRepository = reservationRepository;
		this.userService = userService;
	}
	
	/**
	 * Gets the reservations by user id.
	 *
	 * @param id the id
	 * @return the reservations by user id
	 */
	public List<Reservation> getReservationsByUserId(String id) {
		return reservationRepository.findByUserId(id);	
	}
	
	/**
	 * Gets the upcoming reservations by user id.
	 *
	 * @param id the id
	 * @return the upcoming reservations by user id
	 */
	public List<Reservation> getUpcomingReservationsByUserId(String id) {
		return reservationRepository.findAllByUserIdAndUpcoming(id, LocalDateTime.now());
	}
	
	/**
	 * Gets the past reservations by user id.
	 *
	 * @param id the id
	 * @return the past reservations by user id
	 */
	public List<Reservation> getPastReservationsByUserId(String id) {
		return reservationRepository.findAllByUserIdAndPast(id, LocalDateTime.now()); 
	}
	
	/**
	 * Gets the reservation by id.
	 *
	 * @param id the id
	 * @return the reservation by id
	 */
	public Reservation getReservationById(int id) {
		return reservationRepository.findById(id);
	}
	
	/**
	 * Gets the reservation resource ids.
	 *
	 * @param startDateTime the start date time
	 * @param endDateTime the end date time
	 * @return the reservation resource ids
	 */
	public List<Integer> getReservationResourceIds(LocalDateTime startDateTime, 
			LocalDateTime endDateTime) {
		return reservationRepository
				.findResourceIdsByStartTimeAfterAndEndTimeBefore
				(startDateTime, endDateTime);
	}
	
	/**
	 * Persist reservation in the database.
	 *
	 * @param reservation the reservation
	 * @return the reservation
	 */
	public Reservation saveReservation(Reservation reservation) {
		return reservationRepository.save(reservation);		
	}

	/**
	 * Cancel reservation by id.
	 *
	 * @param id the id
	 * @return the int
	 */
	public int cancelReservation(int id) {
		return reservationRepository.cancel(id);
	}

	
	/**
	 * Gets the reservation by criteria.
	 *
	 * @param reservation the reservation
	 * @return the reservation by criteria
	 */
	public List<Reservation> getReservationByCriteria(Reservation reservation) {
		return reservationRepository.findAll(Example.of(reservation));
	}

	/**
	 * Gets the all.
	 *
	 * @return the all
	 */
	public List<Reservation> getAll() {
		return reservationRepository.findAll();
	}
	
	/**
	 * Posts a ReservationEmail object to the Email service
	 * in order to send a confirmation email to the user
	 * @param reservation
	 * @param resource
	 * @author Austin D. 1811-Java-Nick 1/3/19 
	 */
	public void postToEmailService(Reservation reservation, Resource resource) {
		String buildingName = resource.getBuilding().getName();
		String resourceName = resource.getName();
		String userEmail = userService.findUserById(reservation.getUserId()).getEmail();
		ReservationEmail reservationEmail = new ReservationEmail(userEmail, reservation.getStartTime(), reservation.getEndTime(), buildingName, resourceName);
		new RestTemplate().postForLocation(URI.create("http://localhost:8080/email/sendconfirmation"), reservationEmail);
		
	}


}
