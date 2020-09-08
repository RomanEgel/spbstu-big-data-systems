package com.example.lab4server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@SpringBootApplication
class Lab4ServerApp

fun main(args: Array<String>) {
	runApplication<Lab4ServerApp>(*args)
}

@RestController
class DataObjectRestController {
	@GetMapping("/data/{id}")
	@ResponseBody
	fun findById(@PathVariable id: Long, req: HttpServletRequest, res: HttpServletResponse): DataObject {
		if (req.getHeader("Test") != null) {
			res.setHeader("Test", "tuta")
		}

		return DataObject(id,  UUID.randomUUID().toString())
	}
}

data class DataObject(val id: Long, val name: String)