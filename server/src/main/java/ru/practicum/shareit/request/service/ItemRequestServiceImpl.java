package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private static final Sort SORT_CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");

    private User ensureUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + userId));
    }

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto dto) {
        User user = ensureUser(userId);
        ItemRequest request = ItemRequestMapper.toModel(dto);
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        ItemRequest saved = requestRepository.save(request);
        log.info("Создан запрос на вещь id={} пользователем id={}", saved.getId(), userId);
        return ItemRequestMapper.toDto(saved, Collections.emptyList());
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        ensureUser(userId);
        List<ItemRequest> requests = requestRepository.findByRequestorId(userId, SORT_CREATED_DESC);
        Map<Long, List<ItemDto>> itemsByRequest = loadItemsGrouped(requests);
        return requests.stream()
                .map(r -> ItemRequestMapper.toDto(r, itemsByRequest.get(r.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        ensureUser(userId);
        Pageable page = PageRequest.of(from / size, size, SORT_CREATED_DESC);
        List<ItemRequest> requests = requestRepository.findByRequestorIdNot(userId, page).getContent();
        Map<Long, List<ItemDto>> itemsByRequest = loadItemsGrouped(requests);
        return requests.stream()
                .map(r -> ItemRequestMapper.toDto(r, itemsByRequest.get(r.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        ensureUser(userId);
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден: id=" + requestId));
        Map<Long, List<ItemDto>> itemsByRequest = loadItemsGrouped(List.of(request));
        return ItemRequestMapper.toDto(request, itemsByRequest.get(request.getId()));
    }

    private Map<Long, List<ItemDto>> loadItemsGrouped(List<ItemRequest> requests) {
        if (requests.isEmpty()) return Collections.emptyMap();
        List<Long> requestIds = requests.stream().map(ItemRequest::getId).toList();
        return itemRepository.findByRequestIdIn(requestIds).stream()
                .collect(Collectors.groupingBy(
                        ru.practicum.shareit.item.model.Item::getRequestId,
                        Collectors.mapping(ItemMapper::toDto, Collectors.toList())
                ));
    }
}